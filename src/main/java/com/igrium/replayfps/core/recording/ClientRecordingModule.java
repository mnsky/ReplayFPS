package com.igrium.replayfps.core.recording;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.channel.ChannelHandlers;
import com.igrium.replayfps.core.events.ChannelRegistrationCallback;
import com.igrium.replayfps.core.events.RecordingEvents;
import com.mojang.logging.LogUtils;
import com.replaymod.core.Module;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replaystudio.replay.ReplayFile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientRecordingModule extends EventRegistrations implements Module {
    public static final String ENTRY = "client.ccap";

    private static ClientRecordingModule instance;

    public static ClientRecordingModule getInstance() {
        return instance;
    }

    private ClientCapRecorder activeRecording;

    @Override
    public void initCommon() {
        instance = this;
    }

    @Override
    public void register() {
        super.register();
        WorldRenderEvents.END.register(this::onFrame);
    }

    private ClientCapHeader queuedHeader;

    {
        on(RecordingEvents.STARTED_RECORDING, this::onStartedRecording);
    }

    protected void onStartedRecording(PacketListener listener, ReplayFile file) {
        List<ChannelHandler<?>> channels = new LinkedList<>();

        ChannelRegistrationCallback.EVENT.invoker().createChannels(handler -> {
            if (!ChannelHandlers.REGISTRY.inverse().containsKey(handler)) {
                throw new IllegalArgumentException("The supplied channel handler has not been registered!");
            }
            channels.add(handler);
        });
        LogUtils.getLogger().info("Starting client-cap recording!");
        ClientCapHeader header = new ClientCapHeader(channels);
        try {
            OutputStream out = file.write(ENTRY);
            activeRecording = new ClientCapRecorder(out, listener);
            queuedHeader = header;
            LogUtils.getLogger().info("Header has {} channels", channels.size());
        } catch (Exception e) {
            LogUtils.getLogger().error("Unable to initialize client-cap recording.", e);
        }
    }

    {
        on(RecordingEvents.STOP_RECORDING, this::onStoppingRecording);
    }

    protected void onStoppingRecording(PacketListener listener, ReplayFile file) {
        if (isRecording()) stopRecording();
    }

    protected void onFrame(WorldRenderContext context) {
        if (isRecording()) {
            var client = MinecraftClient.getInstance();
            if (activeRecording.getHeader() == null) {
                assert client.player != null;
                initRecording(activeRecording, client.player.getId());
            }
            activeRecording.tick(client);
        }
    }

    private void initRecording(ClientCapRecorder recording, int localPlayerId) {
        queuedHeader.setLocalPlayerID(localPlayerId);
        recording.writeHeader(queuedHeader);
        recording.startRecording();
    }

    public boolean isRecording() {
        return activeRecording != null;
    }

    /**
     * Stop recording the client-cap.
     *
     * @throws IllegalStateException If we're not currently recording.
     */
    public void stopRecording() throws IllegalStateException {
        if (!isRecording()) {
            throw new IllegalStateException("We are not recording.");
        }
        try {
            activeRecording.close();
        } catch (IOException e) {
            LogUtils.getLogger().error("Error closing recording stream.", e);
        }
        activeRecording = null;
    }
}

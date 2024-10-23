package com.igrium.replayfps.core.networking;

import com.igrium.replayfps.core.networking.event.FakePacketRegistrationCallback;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.util.PlaybackUtils;
import com.mojang.logging.LogUtils;
import com.replaymod.recording.ReplayModRecording;
import com.replaymod.recording.packet.PacketListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FakePacketManager {
    public static final String NAMESPACE = "rp_replayfps";

    public static enum SpectatorRule {APPLY, SKIP}

    private final MinecraftClient client;
    private final ClientPlaybackModule module;
    private final ClientCapPlayer clientCap;

    private final Map<Identifier, FakePacketHandler<CustomPayload>> handlers = Collections.synchronizedMap(new HashMap<>());
    private final Map<Identifier, SpectatorRule> spectatorRules = Collections.synchronizedMap(new HashMap<>());

    public Map<Identifier, SpectatorRule> getSpectatorRules() {
        return spectatorRules;
    }

    public FakePacketManager(MinecraftClient client, ClientPlaybackModule module, ClientCapPlayer clientCap) {
        this.client = client;
        this.module = module;
        this.clientCap = clientCap;
    }

    /**
     * Register all listeners after an instance has been created.
     */
    public void initReceivers() {
        FakePacketRegistrationCallback.EVENT.invoker().register(this);
    }

    /**
     * Determine if a given packet should be parsed as a fake packet.
     *
     * @param id Packet ID.
     * @return If this is a fake packet.
     */
    public static boolean isFakePacket(Identifier id) {
        return id.getNamespace().equals(NAMESPACE);
    }

    /**
     * Process an incoming custom packet.
     *
     * @param payload Payload to process.
     * @return If this packet was consumed as a fake packet.
     */
    public boolean processPacket(CustomPayload payload) {
        Identifier id = payload.getId().id();
        if (!isFakePacket(id)) return false;
        @Nullable
        var handler = handlers.get(id);
        if (handler == null) return false;
        // TODO: Do we want to keep some of this on the netty thread?
        client.execute(() -> {
            Optional<PlayerEntity> playerOpt = module.getLocalPlayer();
            if (playerOpt.isEmpty())
                return;
            PlayerEntity player = playerOpt.get();
            SpectatorRule rule = spectatorRules.getOrDefault(player, SpectatorRule.APPLY);
            if (client.getCameraEntity() != player && rule != SpectatorRule.APPLY)
                return;
            try {
                handler.handle(payload, module, clientCap, player);
            } catch (Throwable ex) {
                LogUtils.getLogger().error("Error handling fake packet: " + id, ex);
            }
        });
        return true;
    }

    public <T extends CustomPayload> void registerReceiver(CustomPayload.Id<T> id, FakePacketHandler<T> handler) {
        handlers.put(id.id(), (FakePacketHandler<CustomPayload>) handler);
    }

    /**
     * Set the behavior for when a fake packet is received while not spectating the player.
     *
     * @param id            ID of the packet to apply to.
     * @param spectatorRule Spectator rule.
     */
    public <T extends CustomPayload> void addSpectatorRule(Identifier id, SpectatorRule spectatorRule) {
        spectatorRules.put(id, Objects.requireNonNull(spectatorRule));
    }

    /**
     * Inject a fake packet into the replay packet stream.
     *
     * @param packet Fake packet.
     */
    public static void injectFakePacket(CustomPayload packet) {
        PacketListener listener = ReplayModRecording.instance.getConnectionEventHandler().getPacketListener();
        // Don't want to re-send packets during replay playback.
        if (PlaybackUtils.isPlayingReplay()) return;
        listener.save(new CustomPayloadS2CPacket(packet));
    }
}

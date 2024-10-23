package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.screen.PlaybackScreenManager;
import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenSerializers;
import com.igrium.replayfps.core.screen.ScreenState;
import com.igrium.replayfps.game.event.ClientScreenEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenScreenFakePacket(Identifier screenId, ScreenState state)
        implements CustomPayload {
    public static final Id<OpenScreenFakePacket> ID = new Id<>(Identifier.of("rp_replayfps:open_screen"));
    public static final PacketCodec<RegistryByteBuf, OpenScreenFakePacket> CODEC =
            PacketCodec.of(OpenScreenFakePacket::write, OpenScreenFakePacket::read);

    private static final OpenScreenFakePacket EMPTY = new OpenScreenFakePacket(null, null);

    public void apply(ClientPlaybackModule module, ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        PlaybackScreenManager screenManager = ClientPlaybackModule.getInstance().getPlaybackScreenManager();
        if (screenManager == null)
            return;
        if (state == null) {
            screenManager.clearScreen();
        } else {
            screenManager.openScreen(state);
        }
    }

    public static void registerListener() {
        ClientScreenEvents.SCREEN_CHANGED.register((client, screen) -> {
            if (client.world == null) return;
            var packet = EMPTY;
            if (screen != null) {
                var serializer = ScreenSerializers.get(screen.getClass());
                if (serializer != null) {
                    var screenId = ScreenSerializers.getId(serializer);
                    var state = serializer.serialize(screen);
                    packet = new OpenScreenFakePacket(screenId, state);
                }
            }
            FakePacketManager.injectFakePacket(packet);
        });
    }

    public static OpenScreenFakePacket read(RegistryByteBuf buf) {
        boolean stayOpen = buf.readBoolean();
        if (!stayOpen) return EMPTY;

        Identifier id = buf.readIdentifier();
        ScreenSerializer serializer = ScreenSerializers.get(id);

        if (serializer == null) {
            LogUtils.getLogger().error("Unknown screen serializer: " + id);
            return EMPTY;
        }

        ScreenState state;
        try {
            state = serializer.readBuffer(buf);
            return new OpenScreenFakePacket(id, state);
        } catch (Exception e) {
            LogUtils.getLogger().error("Error opening fake screen.", e);
            return EMPTY;
        }
    }

    public void write(RegistryByteBuf buf) {
        boolean stayOpen = state != null;
        buf.writeBoolean(stayOpen);
        if (!stayOpen)
            return;
        buf.writeIdentifier(screenId);
        state.writeBuffer(buf);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}

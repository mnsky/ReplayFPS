package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.screen.PlaybackScreenManager;
import com.igrium.replayfps.core.screen.ScreenSerializers;
import com.igrium.replayfps.core.screen.ScreenState;
import com.igrium.replayfps.game.event.ClientScreenEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdateScreenFakePacket(Identifier screenId, ScreenState state)
        implements CustomPayload {
    public static final Id<UpdateScreenFakePacket> ID = new Id<>(Identifier.of("rp_replayfps:update_screen"));
    public static final PacketCodec<RegistryByteBuf, UpdateScreenFakePacket> CODEC =
            PacketCodec.of(UpdateScreenFakePacket::write, UpdateScreenFakePacket::read);

    public void apply(ClientPlaybackModule module, ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        try {
            PlaybackScreenManager screenManager = module.getPlaybackScreenManager();
            if (screenManager == null)
                return;
            Screen screen = screenManager.getScreen().orElse(null);
            if (screen == null)
                return;
            state.apply(MinecraftClient.getInstance(), screen);
        } catch (Exception e) {
            LogUtils.getLogger().error("Error loading screen.", e);
        }
    }

    public static void registerListener() {
        ClientScreenEvents.SCREEN_UPDATED.register((client, screen, oldState, state) -> {
            var serializer = ScreenSerializers.get(screen.getClass());
            if (serializer == null) return;
            var screenId = ScreenSerializers.getId(serializer);
            FakePacketManager.injectFakePacket(
                    new UpdateScreenFakePacket(screenId, state));
        });
    }

    public static UpdateScreenFakePacket read(RegistryByteBuf buf) {
        var screenId = buf.readIdentifier();
        var serializer = ScreenSerializers.get(screenId);
        if (serializer == null) return null;
        var state = serializer.readBuffer(buf);
        return new UpdateScreenFakePacket(screenId, state);
    }

    public void write(RegistryByteBuf buf) {
        buf.writeIdentifier(screenId);
        state.writeBuffer(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

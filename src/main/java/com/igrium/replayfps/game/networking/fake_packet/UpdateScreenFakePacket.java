package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.screen.PlaybackScreenManager;
import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenSerializers;
import com.igrium.replayfps.game.event.ClientScreenEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdateScreenFakePacket<S extends Screen, T>(ScreenSerializer<S, T> serializer, Object value) implements CustomPayload {
    public static final Id<UpdateScreenFakePacket<?, ?>> ID = new Id<>(Identifier.of("rp_replayfps:update_screen"));
    public static final PacketCodec<RegistryByteBuf, UpdateScreenFakePacket<?, ?>> CODEC = PacketCodec.of(UpdateScreenFakePacket::write, UpdateScreenFakePacket::read);

    public Class<PacketByteBuf> getType() {
        return PacketByteBuf.class;
    }

    public void apply(ClientPlaybackModule module, ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        try {
            PlaybackScreenManager screenManager = module.getPlaybackScreenManager();
            if (screenManager == null)
                return;
            Screen screen = screenManager.getScreen().orElse(null);
            if (screen == null)
                return;
            serializer.apply(MinecraftClient.getInstance(), serializer.getSerializedType().cast(value), serializer.getScreenType().cast(screen));
        } catch (Exception e) {
            LogUtils.getLogger().error("Error loading screen.", e);
        }
    }

    public static void registerListener() {
        ClientScreenEvents.SCREEN_UPDATED.register((client, screen, oldVal, newVal) -> {
            var serializer = ScreenSerializers.get(screen.getClass());
            if (serializer == null) return;
            FakePacketManager.injectFakePacket(new UpdateScreenFakePacket<>(serializer, newVal));
        });
    }

    public static UpdateScreenFakePacket<?, ?> read(RegistryByteBuf buf) {
        var screenId = buf.readIdentifier();
        ScreenSerializer<?, ?> serializer = ScreenSerializers.get(screenId);
        if (serializer == null) return null;
        try {
            Object serialized = serializer.readBuffer(buf);
            return new UpdateScreenFakePacket<>(serializer, serialized);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void write(RegistryByteBuf buf) {
        buf.writeIdentifier(ScreenSerializers.getId(serializer));
        serializer.writeBuffer(serializer.getSerializedType().cast(value), buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

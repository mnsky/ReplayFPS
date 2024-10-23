package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.screen.PlaybackScreenManager;
import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenSerializers;
import com.igrium.replayfps.game.event.ClientScreenEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenScreenFakePacket<S extends Screen, T>(ScreenSerializer<S, T> serializer, Object value) implements CustomPayload  {
    public static final Id<OpenScreenFakePacket<?, ?>> ID = new Id<>(Identifier.of("rp_replayfps:open_screen"));
    public static final PacketCodec<RegistryByteBuf, OpenScreenFakePacket<?, ?>> CODEC = PacketCodec.of(OpenScreenFakePacket::write, OpenScreenFakePacket::read);

    private static final OpenScreenFakePacket<?, ?> EMPTY = new OpenScreenFakePacket<>(null, null);

    public void apply(ClientPlaybackModule module, ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        PlaybackScreenManager screenManager = ClientPlaybackModule.getInstance().getPlaybackScreenManager();
        if (screenManager == null)
            return;
        if (serializer == null || value == null) {
            screenManager.clearScreen();
        } else {
            screenManager.openScreen(serializer, value);
        }
    }

    public static void registerListener() {
        ClientScreenEvents.SCREEN_CHANGED.register((client, oldScreen, screen) -> {
            if (client.world == null) return;
            OpenScreenFakePacket<?, ?> packet = EMPTY;
            if (screen != null) {
                var serializer = ScreenSerializers.get(screen.getClass());
                if (serializer != null) {
                    Object value = readScreenData(serializer, screen);
                    packet = new OpenScreenFakePacket<>(serializer, value);
                }
            }
            FakePacketManager.injectFakePacket(packet);
        });
    }

    private static <S extends Screen, T> T readScreenData(ScreenSerializer<S, T> serializer, Screen screen) {
        return serializer.serialize(serializer.getScreenType().cast(screen));
    }

    public static OpenScreenFakePacket<?, ?> read(RegistryByteBuf buf) {
        boolean stayOpen = buf.readBoolean();
        if (!stayOpen) return EMPTY;

        Identifier id = buf.readIdentifier();
        ScreenSerializer<?, ?> serializer = ScreenSerializers.get(id);

        if (serializer == null) {
            LogUtils.getLogger().error("Unknown screen serializer: " + id);
            return EMPTY;
        }

        Object val;
        try {
            val = serializer.readBuffer(buf);
            return new OpenScreenFakePacket<>(serializer, val);
        } catch (Exception e) {
            LogUtils.getLogger().error("Error opening fake screen.", e);
            return EMPTY;
        }
    }

    public void write(RegistryByteBuf buf) {
        boolean stayOpen = serializer != null;
        buf.writeBoolean(stayOpen);
        if (!stayOpen)
            return;

        Identifier id = ScreenSerializers.getId(serializer);
        if (id == null) {
            throw new IllegalStateException("Screen serializer has not been registered!");
        }
        buf.writeIdentifier(id);
        serializer.writeBuffer(serializer.getSerializedType().cast(value), buf);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}

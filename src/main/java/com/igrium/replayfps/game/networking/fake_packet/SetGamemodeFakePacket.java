package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;
import com.igrium.replayfps.game.event.ClientPlayerEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public record SetGamemodeFakePacket(GameMode gamemode) implements CustomPayload {
    public static final CustomPayload.Id<SetGamemodeFakePacket> ID = new CustomPayload.Id<>(Identifier.of("rp_replayfps:set_gamemode"));
    public static final PacketCodec<RegistryByteBuf, SetGamemodeFakePacket> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT.xmap(GameMode::byId, (gm)->gm.getId()), SetGamemodeFakePacket::gamemode, SetGamemodeFakePacket::new);
    
    public static void apply(SetGamemodeFakePacket packet, ClientPlaybackModule module,
            ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        module.setHudGamemode(packet.gamemode());
    }

    public static void registerListener() {
        ClientPlayerEvents.SET_GAMEMODE.register((player, oldGamemode, newGamemode) -> {
            FakePacketManager.injectFakePacket(new SetGamemodeFakePacket(newGamemode));
        });

        ClientJoinedWorldEvent.EVENT.register((client, world) -> {
            FakePacketManager.injectFakePacket(new SetGamemodeFakePacket(
                    client.interactionManager.getCurrentGameMode()));
        });
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}

package com.igrium.replayfps.core.networking;

import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;

@FunctionalInterface
public interface FakePacketHandler<T extends CustomPayload> {
    void handle(T packet, ClientPlaybackModule module,
                ClientCapPlayer clientCap, PlayerEntity localPlayer);
}

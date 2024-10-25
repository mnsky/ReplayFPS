package com.igrium.replayfps.game.channel.handler;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.channel.type.ChannelTypes;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class PlayerPosChannelHandler implements ChannelHandler<Vec3d> {
    @Override
    public ChannelType<Vec3d> getChannelType() {
        return ChannelTypes.VEC3D;
    }

    @Override
    public Vec3d capture(MinecraftClient client) {
        if (client.player == null)
            throw new RuntimeException("No client player");
        return client.player.getPos();
    }

    @Override
    public void apply(Vec3d val, ClientPlaybackContext context) {
        if (context.localPlayer().isPresent())
            context.localPlayer().get().setPosition(val);
    }

    @Override
    public boolean applyPerTick() {
        return true;
    }
}

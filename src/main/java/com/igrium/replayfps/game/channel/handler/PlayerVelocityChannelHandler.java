package com.igrium.replayfps.game.channel.handler;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.channel.type.ChannelTypes;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class PlayerVelocityChannelHandler implements ChannelHandler<Vec3d> {
    @Override
    public ChannelType<Vec3d> getChannelType() {
        return ChannelTypes.VEC3D;
    }

    @Override
    public Vec3d capture(MinecraftClient client) {
        if (client.player == null)
            throw new RuntimeException("No client player");
        return client.player.getVelocity();
    }

    @Override
    public void apply(Vec3d val, ClientPlaybackContext context) {
        context.localPlayer().ifPresent(player -> player.setVelocity(val));
    }

    @Override
    public boolean shouldInterpolate() {
        return true;
    }
}

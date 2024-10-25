package com.igrium.replayfps.game.channel.handler;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.channel.type.ChannelTypes;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class PlayerRotChannelHandler implements ChannelHandler<Vector2fc> {
    @Override
    public ChannelType<Vector2fc> getChannelType() {
        return ChannelTypes.VECTOR2F;
    }

    @Override
    public Vector2fc capture(MinecraftClient client) {
        if (client.player == null)
            throw new RuntimeException("No client player");
        return new Vector2f(client.player.getPitch(), client.player.getYaw());
    }

    @Override
    public void apply(Vector2fc val, ClientPlaybackContext context) {
        context.localPlayer().ifPresent(player -> {
            player.setPitch(val.x());
            player.setYaw(val.y());

            player.prevPitch = val.x();
            player.prevYaw = val.y();

            // For some reason, yaw doesn't render properly if we don't do this.
            player.setHeadYaw(val.y());
            player.prevHeadYaw = val.y();
        });
    }

    @Override
    public boolean shouldInterpolate() {
        return true;
    }
}

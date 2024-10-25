package com.igrium.replayfps.game.channel.handler;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.channel.type.ChannelTypes;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import net.minecraft.client.MinecraftClient;

public class HorizontalSpeedHandler implements ChannelHandler<Float> {
    @Override
    public ChannelType<Float> getChannelType() {
        return ChannelTypes.FLOAT;
    }

    @Override
    public Float capture(MinecraftClient client) {
        if (client.player == null)
            throw new RuntimeException("No client player");
        return client.player.horizontalSpeed;
    }

    @Override
    public void apply(Float val, ClientPlaybackContext context) {
        context.localPlayer().ifPresent(player -> {
            player.prevHorizontalSpeed = player.horizontalSpeed;
            player.horizontalSpeed = val;
        });
    }

    @Override
    public boolean applyPerTick() {
        return true;
    }
}

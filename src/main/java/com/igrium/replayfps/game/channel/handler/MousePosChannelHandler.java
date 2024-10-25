package com.igrium.replayfps.game.channel.handler;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.channel.type.ChannelTypes;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.screen.PlaybackScreenManager;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class MousePosChannelHandler implements ChannelHandler<Vector2fc> {
    @Override
    public ChannelType<Vector2fc> getChannelType() {
        return ChannelTypes.VECTOR2F;
    }

    @Override
    public Vector2fc capture(MinecraftClient client) {
        var x = (float) (client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth());
        var y = (float) (client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight());
        x -= (float) (client.getWindow().getScaledWidth() / 2);
        y -= (float) (client.getWindow().getScaledHeight() / 2);
        return new Vector2f(x, y);
    }

    @Override
    public void apply(Vector2fc val, ClientPlaybackContext context) {
        ClientPlaybackModule module = ClientPlaybackModule.getInstance();
        PlaybackScreenManager screenManager = module.getPlaybackScreenManager();
        if (screenManager == null) return;

        screenManager.setMouseX(val.x());
        screenManager.setMouseY(val.y());
    }

    @Override
    public boolean shouldInterpolate() {
        return true;
    }

}

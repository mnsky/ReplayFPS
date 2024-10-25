package com.igrium.replayfps.core.channel;

import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import net.minecraft.client.MinecraftClient;

/**
 * Handles the application and capturing of a specific animation channel.
 * Handlers are <em>not</em> tied to any given recording instance; they are
 * registered globally.
 */
public interface ChannelHandler<T> {
    ChannelType<T> getChannelType();

    T capture(MinecraftClient client);

    void apply(T val, ClientPlaybackContext context);

    default Class<T> getType() {
        return getChannelType().getType();
    }

    default boolean shouldInterpolate() {
        return false;
    }

    /**
     * If true, this channel applies every client tick instead of every frame.
     */
    default boolean applyPerTick() {
        return false;
    }
}

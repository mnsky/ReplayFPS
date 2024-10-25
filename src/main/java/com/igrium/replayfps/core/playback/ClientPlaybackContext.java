package com.igrium.replayfps.core.playback;

import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.Optional;

public interface ClientPlaybackContext {
    /**
     * The player that the client was controlling during recording.
     */
    Optional<AbstractClientPlayerEntity> localPlayer();

    /**
     * The current timestamp in the replay (milliseconds).
     */
    int timestamp();
}

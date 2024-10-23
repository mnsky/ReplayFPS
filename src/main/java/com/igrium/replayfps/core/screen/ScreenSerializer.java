package com.igrium.replayfps.core.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.RegistryByteBuf;

/**
 * Allows a screen's state to be saved and restored during a replay.
 */
public interface ScreenSerializer {
    /**
     * Read a serialized screen object from a buffer.
     *
     * @param buffer Buffer to read from.
     * @return Serialized screen object.
     */
    ScreenState readBuffer(RegistryByteBuf buffer);

    /**
     * Serialize a screen's current state.
     *
     * @param screen Screen to serialize.
     * @return Serialized screen object.
     */
    ScreenState serialize(Screen screen);
}

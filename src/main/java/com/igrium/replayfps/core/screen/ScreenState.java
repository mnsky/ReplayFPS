package com.igrium.replayfps.core.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.RegistryByteBuf;

public interface ScreenState {
    void writeBuffer(RegistryByteBuf buffer);

    void apply(MinecraftClient client, Screen screen);

    Screen create(MinecraftClient client);

    boolean hasChanged(Screen screen);
}

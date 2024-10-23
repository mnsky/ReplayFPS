package com.igrium.replayfps.game;

import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenSerializers;
import com.igrium.replayfps.core.screen.ScreenState;
import com.igrium.replayfps.game.event.ClientScreenEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ClientScreenListener {
    private static ScreenState state;
    private static Screen screen;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientScreenListener::onTick);
    }

    public static void onTick(MinecraftClient client) {
        var screenChanged = client.currentScreen != screen;
        screen = client.currentScreen;
        if (screenChanged) {
            ClientScreenEvents.SCREEN_CHANGED.invoker()
                    .onScreenChanged(client, screen);
            state = null;
        }
        if (screen == null) return;
        ScreenSerializer serializer = ScreenSerializers.get(screen.getClass());
        if (serializer == null) return;
        var newState = serializer.serialize(screen);
        if (!screenChanged && (state == null || state.hasChanged(screen)))
            ClientScreenEvents.SCREEN_UPDATED.invoker()
                    .onScreenUpdated(client, screen, state, newState);
        state = newState;
    }
}

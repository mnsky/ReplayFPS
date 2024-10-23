package com.igrium.replayfps.game.event;

import com.igrium.replayfps.core.screen.ScreenState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ClientScreenEvents {
    public static final Event<ScreenChangedEvent> SCREEN_CHANGED = EventFactory.createArrayBacked(
            ScreenChangedEvent.class, listeners -> (client, newScreen) -> {
                for (var l : listeners) {
                    l.onScreenChanged(client, newScreen);
                }
            });

    public static final Event<ScreenUpdatedEvent> SCREEN_UPDATED = EventFactory.createArrayBacked(
            ScreenUpdatedEvent.class, listeners -> (client, screen, oldVal, newVal) -> {
                for (var l : listeners) {
                    l.onScreenUpdated(client, screen, oldVal, newVal);
                }
            });

    public static interface ScreenChangedEvent {
        void onScreenChanged(MinecraftClient client, Screen newScreen);
    }

    public static interface ScreenUpdatedEvent {
        void onScreenUpdated(MinecraftClient client, Screen screen, ScreenState oldState, ScreenState newState);
    }
}

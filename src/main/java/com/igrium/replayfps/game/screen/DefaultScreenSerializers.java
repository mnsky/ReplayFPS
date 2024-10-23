package com.igrium.replayfps.game.screen;

import com.igrium.replayfps.core.screen.ScreenSerializers;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.util.Identifier;

public class DefaultScreenSerializers {
    public static void register() {
        ScreenSerializers.register(Identifier.of("minecraft:sign_edit"),
                new SignEditScreenSerializer<>(SignEditScreen::new, SignEditScreen.class));
        ScreenSerializers.register(Identifier.of("minecraft:hanging_sign_edit"),
                new SignEditScreenSerializer<>(HangingSignEditScreen::new, HangingSignEditScreen.class));
        ScreenSerializers.register(Identifier.of("minecraft:death_screen"), new DeathScreenSerializer());
    }
}

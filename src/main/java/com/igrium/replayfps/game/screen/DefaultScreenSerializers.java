package com.igrium.replayfps.game.screen;

import com.igrium.replayfps.core.screen.ScreenSerializers;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.util.Identifier;

public class DefaultScreenSerializers {
    public static void register() {
        ScreenSerializers.register(Identifier.of("minecraft:sign_edit"),
                SignEditScreen.class,
                new SignEditScreenSerializer());
        ScreenSerializers.register(Identifier.of("minecraft:hanging_sign_edit"),
                HangingSignEditScreen.class,
                new SignEditScreenSerializer());
        ScreenSerializers.register(Identifier.of("minecraft:death_screen"),
                DeathScreen.class,
                new DeathScreenSerializer());
    }
}

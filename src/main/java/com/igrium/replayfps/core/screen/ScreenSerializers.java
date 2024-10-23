package com.igrium.replayfps.core.screen;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public final class ScreenSerializers {
    private static final BiMap<Identifier, ScreenSerializer> REGISTRY = HashBiMap.create();
    private static final BiMap<Class<? extends Screen>, ScreenSerializer> CLASS_REGISTRY = HashBiMap.create();

    public static <T extends Screen> void register(
            Identifier id, Class<T> screen, ScreenSerializer serializer) {
        REGISTRY.put(id, serializer);
        CLASS_REGISTRY.put(screen, serializer);
    }

    public static ScreenSerializer get(Identifier id) {
        return REGISTRY.get(id);
    }

    public static <T extends Screen> ScreenSerializer get(Class<T> screenType) {
        return CLASS_REGISTRY.get(screenType);
    }

    public static Identifier getId(ScreenSerializer serializer) {
        return REGISTRY.inverse().get(serializer);
    }
}
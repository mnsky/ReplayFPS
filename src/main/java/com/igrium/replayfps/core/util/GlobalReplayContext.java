package com.igrium.replayfps.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.WeakHashMap;

public class GlobalReplayContext {
    /**
     * Because entity positions are updated every client tick (rather than frame),
     * they may be cached here for the next tick.
     */
    public static final Map<Entity, Vec3d> ENTITY_POS_OVERRIDES = new WeakHashMap<>();
}

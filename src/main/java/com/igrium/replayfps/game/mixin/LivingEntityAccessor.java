package com.igrium.replayfps.game.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor("lastDamageTaken")
    public float getLastDamageTaken();

    @Accessor("lastDamageTaken")
    public void setLastDamageTaken(float lastDamageTaken);
}

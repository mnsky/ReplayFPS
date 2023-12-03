package com.igrium.replayfps.game.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.game.event.SetExperienceEvent;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "setExperience", at = @At("RETURN"))
    void replayfps$onSetExperience(float progress, int total, int level, CallbackInfo ci) {
        SetExperienceEvent.EVENT.invoker().onSetExperience(progress, total, level, (PlayerEntity) (Object) this);
    }
}
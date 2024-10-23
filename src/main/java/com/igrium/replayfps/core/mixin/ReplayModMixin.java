package com.igrium.replayfps.core.mixin;

import com.igrium.replayfps.core.util.ReplayModHooks;
import com.replaymod.core.ReplayMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReplayMod.class)
public class ReplayModMixin {
    @Inject(method = "initModules", at = @At("RETURN"), remap = false)
    void afterInit(CallbackInfo ci) {
        ReplayModHooks.waitForInit().complete((ReplayMod) (Object) this);
    }
}

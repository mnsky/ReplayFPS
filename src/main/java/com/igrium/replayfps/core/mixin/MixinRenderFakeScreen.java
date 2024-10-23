package com.igrium.replayfps.core.mixin;

import com.igrium.replayfps.core.events.CustomScreenRenderCallback;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class MixinRenderFakeScreen {
    @Inject(method = "render(Lnet/minecraft/client/render/RenderTickCounter;Z)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;",
            ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    void replayfps$beforeOverlay(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, @Local() DrawContext drawContext, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j) {
            CustomScreenRenderCallback.EVENT.invoker().onRenderCustomScreen(
                    (GameRenderer) (Object) this, drawContext, i, j, tickCounter.getLastDuration());
    }
}

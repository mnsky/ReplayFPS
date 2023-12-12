package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.igrium.replayfps.core.networking.event.CustomPacketReceivedEvent;

import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;
import net.minecraft.util.Identifier;

@Mixin(AbstractChanneledNetworkAddon.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handle",
            at = @At(value = "INVOKE",
                target = "Lnet/fabricmc/fabric/impl/networking/AbstractChanneledNetworkAddon;getHandler(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;"),
            // remap = false,
            cancellable = true)
    protected void replayfps$handle(ResolvablePayload resolvable, CallbackInfoReturnable<Boolean> ci) {
        Identifier id = resolvable.id();
        if (CustomPacketReceivedEvent.EVENT.invoker().onPacketReceived(id, resolvable.resolve(null))) {
            ci.setReturnValue(true);
        }
        // if (CustomPacketReceivedEvent.EVENT.invoker().onPacketReceived(channelName, originalBuf)) {
        //     ci.setReturnValue(true);
        // }
    }
}

package com.igrium.replayfps.core.mixin;

import com.igrium.replayfps.core.networking.event.CustomPacketReceivedEvent;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: Should we make and register a custom subclass instead of mixing in?
@Mixin(value = AbstractChanneledNetworkAddon.class, remap = false)
public class AbstractChanneledNetworkAddonMixin<H> {
    @Inject(method = "handle", at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/impl/networking/AbstractChanneledNetworkAddon;getHandler(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;"),
            remap = true,
            cancellable = true)
    public void replayfps$handle(CustomPayload payload, CallbackInfoReturnable<Boolean> ci) {
        if (CustomPacketReceivedEvent.EVENT.invoker().onPacketReceived(payload)) {
            ci.setReturnValue(true);
        }
    }
}

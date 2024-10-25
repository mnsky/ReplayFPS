package com.igrium.replayfps.game.mixin;

import com.igrium.replayfps.game.event.ClientPlayerEvents;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    int selectedSlot;

    @Unique
    private int prevSelectedSlot = -1;

    @Inject(method = "updateItems", at = @At("RETURN"))
    void replayfps$onUpdateItems(CallbackInfo ci) {
        if (selectedSlot != prevSelectedSlot) {
            ClientPlayerEvents.SELECT_SLOT.invoker().onSelectSlot((PlayerInventory) (Object) this, selectedSlot);
        }
        prevSelectedSlot = selectedSlot;
    }
}

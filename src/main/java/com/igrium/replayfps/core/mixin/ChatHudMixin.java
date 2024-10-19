package com.igrium.replayfps.core.mixin;

import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.replaymod.replay.FullReplaySender;
import com.replaymod.replay.ReplayHandler;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"), cancellable = true)
    void replayfps$onAddMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        // Do not display chat messages if we're currently hurrying.
        // TODO: implement a system where the hud actually ticks properly in this situation rather than suppressing chat.
        ReplayHandler handler = ClientPlaybackModule.getInstance().getCurrentReplay();
        if (handler != null && handler.getReplaySender() instanceof FullReplaySender sender) {
            if (sender.isHurrying()) ci.cancel();
        }
    }
}

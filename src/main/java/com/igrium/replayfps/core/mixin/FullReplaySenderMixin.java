package com.igrium.replayfps.core.mixin;


import com.igrium.replayfps.core.networking.PacketRedirectors;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.replaymod.replay.FullReplaySender;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FullReplaySender.class)
public class FullReplaySenderMixin {
    // Mix into if(BAD_PACKETS.contains(p.getClass())) return null;
    @ModifyExpressionValue(method = "processPacket", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
    private boolean replayfps$checkForRedirect(boolean original, Packet<?> packet) {
        if (PacketRedirectors.shouldRedirect(packet)) {
            PacketRedirectors.REDIRECT_QUEUED.add(packet);
            return false;
        }
        return original;
    }
}

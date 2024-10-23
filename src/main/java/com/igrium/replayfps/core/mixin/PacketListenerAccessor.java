package com.igrium.replayfps.core.mixin;

import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replaystudio.replay.ReplayFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PacketListener.class)
public interface PacketListenerAccessor {

    @Accessor(value = "replayFile", remap = false)
    ReplayFile getReplayFile();
}

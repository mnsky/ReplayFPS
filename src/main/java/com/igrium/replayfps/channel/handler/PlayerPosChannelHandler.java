package com.igrium.replayfps.channel.handler;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.playback.ClientPlaybackContext;
import com.igrium.replayfps.recording.ClientCaptureContext;

import net.minecraft.util.math.Vec3d;

public class PlayerPosChannelHandler implements ChannelHandler<Vec3d> {

    @Override
    public ChannelType<Vec3d> getChannelType() {
        return ChannelTypes.VEC3D;
    }

    @Override
    public Vec3d capture(ClientCaptureContext context) {
        return context.localPlayer().getPos();
    }

    @Override
    public void apply(Vec3d val, ClientPlaybackContext context) {
        if (context.localPlayer().isPresent()) {
            context.localPlayer().get().setPosition(val);
        }
    }
    
}
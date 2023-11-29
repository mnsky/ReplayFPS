package com.igrium.replayfps;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igrium.replayfps.channel.handler.ChannelHandlers;
import com.igrium.replayfps.events.ChannelRegistrationCallback;
import com.igrium.replayfps.playback.ClientPlaybackModule;
import com.igrium.replayfps.recording.ClientRecordingModule;
import com.igrium.replayfps.util.ReplayModHooks;

public class ReplayFPS implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("replayfps");

    private static ReplayFPS instance;

    public static ReplayFPS getInstance() {
        return instance;
    }

    private ClientRecordingModule clientRecordingModule;

    public ClientRecordingModule getClientRecordingModule() {
        return clientRecordingModule;
    }

    private ClientPlaybackModule clientPlaybackModule;

    public ClientPlaybackModule getClientPlaybackModule() {
        return clientPlaybackModule;
    }

    @Override
    public void onInitialize() {
        instance = this;

        ReplayModHooks.onReplayModInit(mod -> {
            clientRecordingModule = new ClientRecordingModule(mod);
            clientRecordingModule.initCommon();
            clientRecordingModule.initClient();
            clientRecordingModule.register();
            
            clientPlaybackModule = new ClientPlaybackModule();
            clientPlaybackModule.initCommon();
            clientPlaybackModule.initClient();
            clientPlaybackModule.register();
        });

        ChannelRegistrationCallback.EVENT.register(consumer -> {
            consumer.accept(ChannelHandlers.PLAYER_POS);
            consumer.accept(ChannelHandlers.PLAYER_ROT);
            consumer.accept(ChannelHandlers.PREV_ROT);
            consumer.accept(ChannelHandlers.PLAYER_VELOCITY);
            // consumer.accept(ChannelHandlers.PLAYER_STRIDE);
        });
    }
}
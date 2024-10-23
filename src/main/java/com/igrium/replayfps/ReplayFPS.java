package com.igrium.replayfps;

import com.igrium.replayfps.config.ReplayFPSConfig;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.recording.ClientRecordingModule;
import com.igrium.replayfps.core.util.ReplayModHooks;
import com.igrium.replayfps.game.BullshitPlayerInventoryManager;
import com.igrium.replayfps.game.ClientScreenListener;
import com.igrium.replayfps.game.channel.DefaultChannelHandlers;
import com.igrium.replayfps.game.networking.DefaultFakePackets;
import com.igrium.replayfps.game.networking.DefaultPacketRedirectors;
import com.igrium.replayfps.game.screen.DefaultScreenSerializers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplayFPS implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ReplayFPS");

    private static ReplayFPS instance;

    public static ReplayFPS getInstance() {
        return instance;
    }

    private ReplayFPSConfig config;

    public ReplayFPSConfig config() {
        return config;
    }

    public static ReplayFPSConfig getConfig() {
        return getInstance().config();
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
        config = ReplayFPSConfig.load();

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

        DefaultChannelHandlers.registerDefaults();
        DefaultPacketRedirectors.registerDefaults();
        DefaultFakePackets.registerDefaults();
        DefaultScreenSerializers.register();

        BullshitPlayerInventoryManager.register();
        ClientScreenListener.register();
    }
}
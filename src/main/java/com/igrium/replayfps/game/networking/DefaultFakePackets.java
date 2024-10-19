package com.igrium.replayfps.game.networking;

import com.igrium.replayfps.core.networking.event.FakePacketRegistrationCallback;
import com.igrium.replayfps.game.networking.fake_packet.SetGamemodeFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.UpdateInventoryFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.UpdateSelectedSlotFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.legacy.UpdateHotbarFakePacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class DefaultFakePackets {
    public static void registerDefaults() {
        PayloadTypeRegistry.playS2C().register(SetGamemodeFakePacket.ID, SetGamemodeFakePacket.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateInventoryFakePacket.ID, UpdateInventoryFakePacket.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateSelectedSlotFakePacket.ID, UpdateSelectedSlotFakePacket.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateHotbarFakePacket.ID, UpdateHotbarFakePacket.CODEC);
        FakePacketRegistrationCallback.EVENT.register(manager -> {
            manager.registerReceiver(UpdateInventoryFakePacket.ID, UpdateInventoryFakePacket::apply);
            manager.registerReceiver(UpdateSelectedSlotFakePacket.ID, UpdateSelectedSlotFakePacket::apply);
            manager.registerReceiver(SetGamemodeFakePacket.ID, SetGamemodeFakePacket::apply);
            manager.registerReceiver(UpdateHotbarFakePacket.ID, UpdateHotbarFakePacket::apply);
        });
        UpdateInventoryFakePacket.registerListener();
        UpdateSelectedSlotFakePacket.registerListener();
        SetGamemodeFakePacket.registerListener();
    }
}

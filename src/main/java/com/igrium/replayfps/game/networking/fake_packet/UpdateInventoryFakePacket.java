package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.ItemSlot;
import com.igrium.replayfps.game.event.InventoryModifiedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.List;

public record UpdateInventoryFakePacket(List<ItemSlot> items) implements CustomPayload {
    public static final CustomPayload.Id<UpdateInventoryFakePacket> ID = new CustomPayload.Id<>(Identifier.of("rp_replayfps:update_inventory"));
    public static final PacketCodec<RegistryByteBuf, UpdateInventoryFakePacket> CODEC = PacketCodec.tuple(ItemSlot.CODEC.collect(PacketCodecs.toList()), UpdateInventoryFakePacket::items, UpdateInventoryFakePacket::new);
    
    public static void apply(UpdateInventoryFakePacket packet, ClientPlaybackModule module,
                             ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        packet.items.forEach((item) -> localPlayer.getInventory().setStack(item.slot(), item.stack().orElse(ItemStack.EMPTY)));
    }

    @SuppressWarnings("resource")
    public static void registerListener() {
        InventoryModifiedEvent.EVENT.register((inventory, updates) -> {
            if (!inventory.player.getWorld().isClient) return;
            FakePacketManager.injectFakePacket(new UpdateInventoryFakePacket(updates));
        });
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}

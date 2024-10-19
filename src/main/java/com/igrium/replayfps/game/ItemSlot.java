package com.igrium.replayfps.game;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import java.util.Optional;

public record ItemSlot (int slot, Optional<ItemStack> stack) {
    public static final PacketCodec<RegistryByteBuf, ItemSlot> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, ItemSlot::slot, ItemStack.PACKET_CODEC.collect(PacketCodecs::optional), ItemSlot::stack, ItemSlot::new);
}

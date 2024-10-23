package com.igrium.replayfps.game.networking.fake_packet.legacy;

import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.util.ItemIdCompatibility;
import com.replaymod.replay.ReplayHandler;
import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replaystudio.lib.viaversion.api.minecraft.item.Item;
import com.replaymod.replaystudio.lib.viaversion.api.protocol.version.ProtocolVersion;
import com.replaymod.replaystudio.lib.viaversion.api.type.Types;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.io.IOException;

public record UpdateHotbarFakePacket(Int2ObjectMap<ItemStack> map) implements CustomPayload {
    public static final Id<UpdateHotbarFakePacket> ID = new Id<>(Identifier.of("rp_replayfps:update_hotbar"));
    public static final PacketCodec<RegistryByteBuf, UpdateHotbarFakePacket> CODEC = PacketCodec.of(UpdateHotbarFakePacket::write, UpdateHotbarFakePacket::read);

    public static void apply(UpdateHotbarFakePacket packet, ClientPlaybackModule module,
                             ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        packet.map.forEach((slot, stack) -> {
            localPlayer.getInventory().setStack(slot, stack);
        });
    }

    public static UpdateHotbarFakePacket read(RegistryByteBuf buf) {
        ReplayHandler replay = ReplayModReplay.instance.getReplayHandler();
        if (replay == null)
            return new UpdateHotbarFakePacket((new Int2ObjectArrayMap<>()));
        ProtocolVersion version;
        try {
            version = replay.getReplayFile().getMetaData().getProtocolVersion();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int size = buf.readInt();
        var map = new Int2ObjectArrayMap<ItemStack>(size);
        for (int i = 0; i < size; i++) {
            int slot = buf.readInt();
            ItemStack stack = ItemStack.EMPTY;
            if (version.newerThanOrEqualTo(ProtocolVersion.v1_20_5)) {
                stack = ItemStack.PACKET_CODEC.decode(buf);
            } else {
                Item readItem;
                if (version.newerThanOrEqualTo(ProtocolVersion.v1_20_2))
                    readItem = Types.ITEM1_20_2.read(buf);
                else
                    readItem = Types.ITEM1_13_2.read(buf);
                if (readItem != null) {
                    int itemId = ItemIdCompatibility.getNewItemId(version, readItem.identifier());
                    int count = readItem.amount();
                    var item = buf.getRegistryManager().get(RegistryKeys.ITEM).getEntry(itemId).orElse(null);
                    if (item != null)
                        stack = new ItemStack(item.value(), count);
                }
            }
            map.put(slot, stack);
        }
        return new UpdateHotbarFakePacket(map);
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(map.size());
        map.forEach((slot, stack) -> {
            buf.writeInt(slot);
            ItemStack.PACKET_CODEC.encode(buf, stack);
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

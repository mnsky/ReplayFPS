package com.igrium.replayfps.game;

import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;
import com.igrium.replayfps.game.event.InventoryModifiedEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Until I get screen handlers working properly, this helps with syncing the player hotbar.
 */
public class BullshitPlayerInventoryManager {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(BullshitPlayerInventoryManager::onEndTick);
        ClientJoinedWorldEvent.EVENT.register((client, world) -> reset());
    }

    private static ItemStack[] prevInventory = Collections.nCopies(36, ItemStack.EMPTY).toArray(new ItemStack[36]);

    private static void onEndTick(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) return;
        PlayerInventory inventory = player.getInventory();
        List<ItemSlot> updates = new ArrayList<>();
        for (int i = 0; i < prevInventory.length; i++) {
            ItemStack newStack = inventory.getStack(i).copy();
            if (!ItemStack.areEqual(prevInventory[i], newStack))
                updates.add(new ItemSlot(i, newStack.isEmpty() ? Optional.empty() : Optional.of(newStack)));
            prevInventory[i] = newStack;
        }
        if (!updates.isEmpty())
            InventoryModifiedEvent.EVENT.invoker().onInventoryModified(player.getInventory(), updates);
    }

    private static void reset() {
        for (int i = 0; i < prevInventory.length; i++) {
            prevInventory[i] = ItemStack.EMPTY;
        }
    }
}

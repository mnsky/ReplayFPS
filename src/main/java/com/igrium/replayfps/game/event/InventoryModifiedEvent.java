package com.igrium.replayfps.game.event;

import com.igrium.replayfps.game.ItemSlot;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerInventory;

import java.util.List;

public interface InventoryModifiedEvent {
    Event<InventoryModifiedEvent> EVENT = EventFactory.createArrayBacked(
            InventoryModifiedEvent.class, listeners -> (inv, updates) -> {
                for (var l : listeners) {
                    l.onInventoryModified(inv, updates);
                }
            });

    void onInventoryModified(PlayerInventory inventory, List<ItemSlot> updates);
}

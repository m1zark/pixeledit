package com.m1zark.pixeledit.listeners;

import com.m1zark.m1utilities.api.Inventories;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;

public class CraftingListener {
    @Listener
    public void onInventoryClick(ClickInventoryEvent event, @First Player player, @Getter("getTargetInventory") Inventory inventory) {
        if (inventory.getArchetype() == InventoryArchetypes.WORKBENCH || inventory.getArchetype() == InventoryArchetypes.ANVIL) {
            event.getTransactions().forEach(slotTransaction -> {
                ItemStack originalStack = slotTransaction.getOriginal().createStack();

                if(originalStack.getType() != ItemTypes.AIR) {
                    if(Inventories.doesHaveNBT(originalStack, "PokeBuilder") || Inventories.doesHaveNBT(originalStack,"typeID")) {
                        event.setCancelled(true);
                    }
                }
            });
        }
    }
}

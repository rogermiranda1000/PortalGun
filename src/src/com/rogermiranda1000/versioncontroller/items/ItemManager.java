package com.rogermiranda1000.versioncontroller.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class ItemManager {
    /**
     * Implement method to get the item(s) in hand
     * @param playerInventory Inventory
     * @return Item(s) holded
     */
    public abstract ItemStack[] getItemInHand(PlayerInventory playerInventory);

    /**
     * Get player's inventory and call getItemInHand(PlayerInventory)
     * @param player Player
     * @return Item(s) holded
     */
    public ItemStack[] getItemInHand(Player player) {
        return this.getItemInHand(player.getInventory());
    }

    /**
     * Check if player is holding an item
     * @param p Player
     * @param i Item
     * @param ignoresQuantity Must the player have the exact same quantity specified on 'i'
     * @return If the player is holding that item (true), or not (false)
     */
    public boolean hasItemInHand(Player p, ItemStack i, boolean ignoresQuantity) {
        for (ItemStack item : this.getItemInHand(p)) {
            if (ignoresQuantity) {
                item = item.clone();
                item.setAmount(1);
            }

            if (item.equals(i)) return true;
        }

        return false;
    }

    /**
     * Check if player is holding an item, ignoring the quantity
     * @param p Player
     * @param i Item
     * @return If the player is holding that item (true), or not (false)
     */
    public boolean hasItemInHand(Player p, ItemStack i) {
        return this.hasItemInHand(p, i, true);
    }
}

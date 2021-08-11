package com.rogermiranda1000.versioncontroller.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * ItemManager for version >= 1.9
 */
public class ItemPost9 extends ItemManager {
    @Override
    public ItemStack[] getItemInHand(PlayerInventory playerInventory) {
        ItemStack[] r = new ItemStack[2];
        r[0] = playerInventory.getItemInMainHand();
        r[1] = playerInventory.getItemInOffHand();
        return r;
    }
}

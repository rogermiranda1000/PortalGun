package com.rogermiranda1000.versioncontroller.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ItemManager for version < 1.9
 */
public class ItemPre9 extends ItemManager {
    @Nullable
    private static final Method getItemInHandMethod = ItemPre9.getItemInHandMethod();

    @Nullable
    private static Method getItemInHandMethod() {
        try {
            return PlayerInventory.class.getMethod("getItemInHand");
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("ConstantConditions") // ignore NPE
    @Override
    public ItemStack[] getItemInHand(PlayerInventory playerInventory) {
        try {
            ItemStack[] r = new ItemStack[1];
            r[0] = (ItemStack) ItemPre9.getItemInHandMethod.invoke(playerInventory);
            return r;
        } catch (IllegalAccessException | NullPointerException | InvocationTargetException e) {
            //e.printStackTrace();
            return new ItemStack[0];
        }
    }
}

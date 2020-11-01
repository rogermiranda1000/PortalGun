package com.rogermiranda1000.portalgun;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Method;
import java.util.function.Function;

public class ItemManager {
    private static Function<PlayerInventory,ItemStack[]> getItemFunction;

    // static class constructor
    static {
        try {
            // version < 1.9
            if(ItemManager.getVersion()<9) getItemFunction = getItemList(PlayerInventory.class.getMethod("getItemInHand"));
            // version >= 1.9
            else getItemFunction = getItemList(PlayerInventory.class.getMethod("getItemInMainHand"), PlayerInventory.class.getMethod("getItemInOffHand"));
        } catch(NoSuchMethodException NSMEx) {
            NSMEx.printStackTrace();
            getItemFunction = playerInventory->new ItemStack[0];
        }
    }

    /**
     * @param methods la forma de acceder a la(s) mano(s)
     * @return función; al ser llamada (retorno.apply(inventory)) retornará un array con los items en la mano
     */
    private static Function<PlayerInventory,ItemStack[]> getItemList(Method... methods) {
        return playerInventory->{
            try {
                ItemStack[] r = new ItemStack[methods.length];
                for (int n = 0; n < methods.length; n++) r[n] = (ItemStack) methods[n].invoke(playerInventory);
                return r;
            } catch (Exception Ex) {
                Ex.printStackTrace();
                return new ItemStack[0];
            }
        };
    }

    /**
     * @return minecraft version (1.XX)
     */
    private static int getVersion() {
        return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    }

    public static ItemStack[] getItemInHand(Player player){
        return getItemInHand(player.getInventory());
    }

    public static ItemStack[] getItemInHand(PlayerInventory playerInventory) {
        return getItemFunction.apply(playerInventory);
    }

    public static boolean hasItemInHand(Player p, ItemStack i) {
        for (ItemStack item : getItemInHand(p)) {
            if (item.equals(i)) return true;
        }
        return false;
    }
}

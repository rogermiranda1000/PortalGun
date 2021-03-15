package com.rogermiranda1000.versioncontroller.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BlockManager for version < 1.13
 */
public class BlockPre13 implements BlockManager {
    @Override
    public @Nullable Object getMaterial(String type) {
        ItemStack r = null;
        String []s = type.split(":");

        try {
            r = new ItemStack(Material.valueOf(s[0]), (short)1, s.length == 2 ? Short.valueOf(s[1]) : 0);
        }
        catch (IllegalArgumentException IAEx) { }

        return r;
    }

    @Override
    public Object getObject(@NotNull Block block) {
        return new ItemStack(block.getType(), (short)1, block.getData());
    }

    @Override
    public boolean isPassable(@NotNull Block block) {
        return !block.getType().isSolid();
    }
}

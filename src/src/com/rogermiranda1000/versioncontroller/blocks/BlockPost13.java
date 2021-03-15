package com.rogermiranda1000.versioncontroller.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BlockManager for version >= 1.13
 */
public class BlockPost13 implements BlockManager {
    @Override
    public @Nullable Object getMaterial(String type) {
        Material r = null;

        try {
            r = Material.valueOf(type);
        }
        catch (IllegalArgumentException IAEx) { }

        return r;
    }

    @Override
    public Object getObject(@NotNull Block block) {
        return block.getType();
    }

    @Override
    public boolean isPassable(@NotNull Block block) {
        return block.isPassable();
    }
}

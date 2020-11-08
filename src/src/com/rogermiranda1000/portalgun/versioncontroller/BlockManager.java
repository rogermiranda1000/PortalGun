package com.rogermiranda1000.portalgun.versioncontroller;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class BlockManager {
    private final static Function<String,Object> getBlockFunction;
    private final static Function<Block, Object> getObjectFunction;
    private final static Function<Block, Boolean> isPassableFunction;

    static {
        // version < 1.13
        if(VersionController.getVersion()<13) {
            isPassableFunction = block->!block.getType().isSolid();

            getBlockFunction = type->{
                String []s = type.split(":");
                try {
                    return new ItemStack(Material.valueOf(s[0]), (short)1, s.length == 2 ? Short.valueOf(s[1]) : 0);
                } catch (IllegalArgumentException IAEx) {
                    PortalGun.printErrorMessage("The block '" + s[0] + ":" + (s.length == 2 ? s[1] : "0") + "' does not exists.");
                    return null;
                }
            };

            getObjectFunction = block -> new ItemStack(block.getType(), (short)1, block.getData());
        }
        // version >= 1.13
        else {
            isPassableFunction = Block::isPassable;

            getBlockFunction = type->{
                try {
                    return Material.valueOf(type);
                } catch (IllegalArgumentException IAEx) {
                    PortalGun.printErrorMessage("The block '" + type + "' does not exists.");
                    return null;
                }
            };

            getObjectFunction = Block::getType;
        }
    }

    public static Object getMaterial(String type) {
        return BlockManager.getBlockFunction.apply(type);
    }

    public static Object getObject(Block b) {
        return BlockManager.getObjectFunction.apply(b);
    }

    public static boolean isPassable(Block b) {
        return isPassableFunction.apply(b);
    }
}

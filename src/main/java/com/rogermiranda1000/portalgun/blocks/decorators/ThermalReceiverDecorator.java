package com.rogermiranda1000.portalgun.blocks.decorators;

import com.rogermiranda1000.portalgun.cubes.Cube;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ThermalReceiverDecorator extends ArmorStandDecorator {
    public static ItemStack THERMAL_RECEIVER = null;

    @Override
    protected Location getSpawnLocation(Location location, Vector direction) {
        return new Location(
                location.getWorld(),
                location.getBlockX() + 0.5f + direction.getX()/4,
                location.getBlockY() + Cube.ARMORSTAND_VERTICAL_OFFSET + 0.1,
                location.getBlockZ() + 0.5f + direction.getZ()/4
        );
    }

    @Override
    protected @NotNull ItemStack getHead() {
        return ThermalReceiverDecorator.THERMAL_RECEIVER;
    }
}

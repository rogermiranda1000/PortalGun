package com.rogermiranda1000.portalgun.blocks.decorators;

import com.rogermiranda1000.portalgun.blocks.beam.Beam;
import com.rogermiranda1000.portalgun.cubes.Cube;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PoweredThermalReceiverDecorator extends ArmorStandDecorator {
    public static ItemStack ACTIVE_THERMAL_RECEIVER = null;

    @Override
    protected Location getSpawnLocation(Location location, Vector direction) {
        Location position = new Location(
                location.getWorld(),
                location.getBlockX() + 0.5f + direction.getX()/4,
                location.getBlockY() + Cube.ARMORSTAND_VERTICAL_OFFSET + 0.1,
                location.getBlockZ() + 0.5f + direction.getZ()/4
        );

        position.setYaw(Beam.vectorToYaw(direction));

        return position;
    }

    @Override
    protected @NotNull ItemStack getHead() {
        return PoweredThermalReceiverDecorator.ACTIVE_THERMAL_RECEIVER;
    }
}

package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class ThermalReceiver {
    public final Location location;
    public final Vector direction;
    public BlockType block;
    public boolean isPowered;

    public ThermalReceiver(Location location, Vector direction) {
        this.location = location;
        this.direction = direction;
        this.isPowered = false;
        this.block = VersionController.get().getObject(location.getBlock());
    }

    public void power() {
        this.isPowered = true;
        this.location.getBlock().setType(Material.REDSTONE_BLOCK);
    }

    public void unpower() {
        this.isPowered = false;
        this.block.setType(this.location.getBlock());
    }

    public Location getPosition() {
        return this.location;
    }
}

package com.rogermiranda1000.portalgun.blocks;

import org.bukkit.Location;

public class ThermalReceiver {
    public final Location location;
    public boolean isPowered;

    public ThermalReceiver(Location location) {
        this.location = location;
        this.isPowered = false;
    }

    public void power() {
        this.isPowered = true;
        // TODO power things
    }

    public void unpower() {
        this.isPowered = false;
        // TODO unpower things
    }

    public Location getPosition() {
        return this.location;
    }
}

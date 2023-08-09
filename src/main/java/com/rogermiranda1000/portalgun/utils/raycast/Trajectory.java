package com.rogermiranda1000.portalgun.utils.raycast;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Trajectory {
    private final Location location;
    private final Vector direction;

    public Trajectory(Location location, Vector direction) {
        this.location = location;
        this.direction = direction;
    }

    public Location getLocation() {
        return this.location;
    }

    public Vector getDirection() {
        return this.direction;
    }
}

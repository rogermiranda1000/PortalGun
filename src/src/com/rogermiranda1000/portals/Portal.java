package com.rogermiranda1000.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public abstract class Portal {
    protected final Location position;
    protected final Direction direction;

    Portal(Location loc, Direction dir) {
        this.position = loc;
        this.direction = dir;
    }

    public abstract boolean insidePortal(Location loc);
}

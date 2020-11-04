package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class FloorPortal extends Portal implements Cloneable {
    public FloorPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    public void playParticle() {

    }

    // TODO: Floor calculations
    public Location []calculateTeleportLocation() {
        return new Location[] {this.getPosition()};
    }

    public Location []calculateSupportLocation() {
        return new Location[] {
                this.getPosition(),
                this.getPosition().add(0.f, 1.f, 0.f)
        };
    }
}

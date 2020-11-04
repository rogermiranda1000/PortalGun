package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class CeilingPortal extends Portal implements Cloneable {
    public CeilingPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    public void playParticle() {

    }

    // TODO: Ceiling calculations
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

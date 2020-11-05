package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class WallPortal extends Portal implements Cloneable {
    public WallPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    // TODO: particulas
    public void playParticle() {
        this.getParticle();
    }

    // TODO: teleport locations
    public Location []calculateTeleportLocation() {
        Location l = this.direction.addOneBlock(this.getPosition());

        return new Location[] {
                l,
                l.clone().add(0.f, 1.f, 0.f)
        };
    }

    // TODO: support locations
    public Location []calculateSupportLocation() {
        return new Location[] {
                this.getPosition(),
                this.getPosition().add(0.f, 1.f, 0.f)
        };
    }
}

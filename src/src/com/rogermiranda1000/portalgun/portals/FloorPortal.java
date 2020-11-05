package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class FloorPortal extends Portal implements Cloneable {
    public FloorPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    public void playParticle() {

    }

    public Location []calculateTeleportLocation() {
        Location l = this.getPosition().add(0.f, 1.f, 0.f);

        return new Location[] {
                l,
                this.direction.getOpposite().addOneBlock(l.clone())
        };
    }

    public Location []calculateSupportLocation() {
        return new Location[] {
                this.getPosition(),
                this.direction.getOpposite().addOneBlock(this.getPosition())
        };
    }
}

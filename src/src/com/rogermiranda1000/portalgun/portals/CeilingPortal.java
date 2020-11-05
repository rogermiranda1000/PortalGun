package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class CeilingPortal extends Portal implements Cloneable {
    public CeilingPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    public void playParticle() {

    }

    public Location []calculateTeleportLocation() {
        // TODO: under water? (y -> -1.f)
        Location l = this.getPosition().add(0.f, -2.f, 0.f);

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

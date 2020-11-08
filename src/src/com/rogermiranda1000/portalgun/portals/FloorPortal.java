package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FloorPortal extends TopPortal implements Cloneable {
    public FloorPortal(Player owner, Location loc, Direction dir, boolean isLeft) {
        super(owner, loc, dir, isLeft);
    }

    protected float getParticleY() {
        return 1.1f;
    }

    public Location []calculateTeleportLocation() {
        Location l = this.getPosition().add(0.f, 1.f, 0.f);

        return new Location[] {
                l,
                this.direction.getOpposite().addOneBlock(l.clone())
        };
    }

    public Vector getApproachVector() {
        return new Vector(0.f, -1.f, 0.f);
    }
}

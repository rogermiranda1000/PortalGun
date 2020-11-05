package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CeilingPortal extends TopPortal implements Cloneable {
    public CeilingPortal(Player owner, Location loc, Direction dir, boolean isLeft) {
        super(owner, loc, dir, isLeft);
    }

    protected float getParticleY() {
        return -0.1f;
    }

    public Location []calculateTeleportLocation() {
        // TODO: under water? (y -> -1.f)
        Location l = this.getPosition().add(0.f, -2.f, 0.f);

        return new Location[] {
                l,
                this.direction.getOpposite().addOneBlock(l.clone())
        };
    }

    public Vector getApproachVector() {
        return new Vector(0.f, 1.f, 0.f);
    }
}

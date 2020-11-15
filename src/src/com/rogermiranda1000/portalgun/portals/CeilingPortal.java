package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.util.Vector;

public class CeilingPortal extends TopPortal implements Cloneable {
    public CeilingPortal(OfflinePlayer owner, Location loc, Direction dir, boolean isLeft) {
        super(owner, loc, dir, isLeft);
    }

    protected float getParticleY() {
        return -0.1f;
    }

    public Location []calculateTeleportLocation() {
        Location l = this.getPosition().add(0.f, -2.f, 0.f);
        Location l2 = this.getPosition().add(0.f, -1.f, 0.f);

        return new Location[] {
                l,
                this.direction.getOpposite().addOneBlock(l.clone()),
                // TODO: other portals? (by default 0 pos)
                l2,
                this.direction.getOpposite().addOneBlock(l2.clone())
        };
    }

    public Vector getApproachVector() {
        return new Vector(0.f, 1.f, 0.f);
    }
}

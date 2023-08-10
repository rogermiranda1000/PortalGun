package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.util.Vector;

public class WallPortal extends Portal implements Cloneable {
    public WallPortal(OfflinePlayer owner, Location loc, Direction dir, boolean isLeft) {
        super(owner, loc, dir, isLeft);
    }

    public Location getParticlePosition(short currentParticle) {
        Location loc = this.getPosition();
        double angle= (2.D * Math.PI) * (double)currentParticle/(double)Portal.iterations;

        loc.add(0.5f, 0.9D*(1.1D+Math.sin(angle)), 0.5f); // center & Y
        loc.add(this.getApproachVector().multiply(-0.6f));
        loc.add(this.getApproachVector().crossProduct(new Vector(0.f, 1.f, 0.f)).normalize().multiply( 0.45D*Math.cos(angle) )); // horizontal

        return loc;
    }

    public Location []calculateTeleportLocation() {
        Location l = this.direction.addOneBlock(this.getPosition());

        return new Location[] {
                l,
                l.clone().add(0.f, 1.f, 0.f)
        };
    }

    public Location []calculateSupportLocation() {
        return new Location[] {
                this.getPosition(),
                this.getPosition().add(0.f, 1.f, 0.f)
        };
    }

    public Vector getApproachVector() {
        return this.direction.getVector().multiply(-1.f);
    }
}

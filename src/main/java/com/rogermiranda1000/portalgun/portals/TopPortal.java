package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.util.Vector;

public abstract class TopPortal extends Portal {
    protected TopPortal(OfflinePlayer owner, Location loc, Direction dir, boolean isLeft) {
        super(owner, loc, dir, isLeft);
    }

    public Location getParticlePosition(short currentParticle) {
        Location loc = this.getPosition();
        double angle= (2.D * Math.PI) * (double)currentParticle/(double)Portal.iterations;

        Vector dir = this.direction.getVector();
        Vector perpendicular = dir.clone().crossProduct(new Vector(0.f, 1.f, 0.f)).normalize();
        // TODO: why?
        if (this.direction == Direction.N || this.direction == Direction.W) perpendicular.multiply(-1);
        loc.add(0.f, this.getParticleY(), 0.f);
        loc.add(perpendicular.multiply( 0.45D*(1.1D+Math.cos(angle)) )); // horizontal
        if (this.direction == Direction.N || this.direction == Direction.E) loc.add(dir);
        loc.add(dir.multiply( -0.9D*(1.1D+Math.sin(angle)) ));

        return loc;
    }

    public Location []calculateSupportLocation() {
        return new Location[] {
                this.getPosition(),
                this.direction.getOpposite().addOneBlock(this.getPosition())
        };
    }

    protected abstract float getParticleY();
    public abstract Location []calculateTeleportLocation();
    public abstract Vector getApproachVector();
}

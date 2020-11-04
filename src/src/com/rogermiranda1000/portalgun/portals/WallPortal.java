package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class WallPortal extends Portal {
    public WallPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    // TODO: particulas
    public void playParticle() {
        this.getParticle();
    }

    // TODO: teleport locations
    public Location getTeleportLocation() {
        return this.position.clone().add(0.f,1.f,0.f);
    }

    public Portal clone() {
        return new WallPortal(this.position, this.direction, this.isLeft);
    }
}

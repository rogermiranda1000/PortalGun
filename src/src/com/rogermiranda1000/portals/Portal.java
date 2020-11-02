package com.rogermiranda1000.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public abstract class Portal {
    private static HashMap<UUID, Portal[]> portals;
    protected static Particle[] particles;

    private Portal linked;
    protected final Location position;
    protected final Direction direction;
    protected final boolean isLeft; // used in the particle's color

    static {
        Portal.portals = new HashMap<UUID, Portal[]>();
        Portal.particles = new Particle[2];
    }

    Portal(Location loc, Direction dir, boolean isLeft) {
        this.position = loc;
        this.direction = dir;
        this.isLeft = isLeft;
    }

    public void setLinked(Portal l) {
        this.linked = l;
    }

    /**
     * @param id user's UUID
     * @param p new portal
     * @param leftPortal is it left-type portal (true), or the right-type (false)
     */
    public void setPortal(UUID id, Portal p, boolean leftPortal) {
        Portal[] userPortals = Portal.portals.get(id);
        int pos = (leftPortal ? 0 : 1); // left portal => pos 0

        if (userPortals == null) {
            userPortals = new Portal[2];
            Portal.portals.put(id, userPortals);
        }
        userPortals[pos] = p;
    }

    /**
     * @param u user
     * @param p new portal
     * @param leftPortal is it left-type portal (true), or the right-type (false)
     */
    public void setPortal(Player u, Portal p, boolean leftPortal) {
        setPortal(u.getUniqueId(), p, leftPortal);
    }

    public abstract boolean insidePortal(Location loc);
    public abstract void playParticle();
}

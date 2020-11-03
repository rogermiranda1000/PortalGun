package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public abstract class Portal {
    private static HashMap<UUID, Portal[]> portals;
    private static HashMap<Location, Portal> portalsLocations;
    private static Particle[] particles;
    public static boolean allParticlesAtOnce;

    private Portal linked;
    protected final Location position;
    protected final Direction direction;
    protected final boolean isLeft; // used in the particle's color

    static {
        Portal.portals = new HashMap<>();
        Portal.portalsLocations = new HashMap<>();
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
        int pos = (leftPortal ? 0 : 1), otherPos = (!leftPortal ? 0 : 1); // left portal => pos 0

        if (userPortals == null) {
            userPortals = new Portal[2];
            Portal.portals.put(id, userPortals);
        }
        else {
            // user have portals; the old one (if exists) must be eliminated
            Portal.removePortal(userPortals[pos]);
            if (userPortals[otherPos] != null) userPortals[otherPos].setLinked(p);
        }

        // TODO: x2 teleport locations
        Portal.portalsLocations.put(p.getTeleportLocation(), p);
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

    /**
     * @param p portal to be eliminated on HashMap (if exists)
     */
    public static void removePortal(Portal p) {
        if (p != null) portalsLocations.remove(p.getTeleportLocation());
    }

    /**
     * @param id key to be remove
     * @return if the key exists
     */
    public static boolean removePortal(UUID id) {
        Portal []r = Portal.portals.remove(id);
        if (r != null) {
            removePortal(r[0]);
            removePortal(r[1]);

            return true;
        }
        return false;
    }

    public static boolean removePortal(Player p) {
        return Portal.removePortal(p.getUniqueId());
    }

    public static void removeAllPortals() {
        Portal.portals.clear();
        Portal.portalsLocations.clear();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.position.getWorld().getName());
        sb.append(',');
        sb.append(this.position.getX());
        sb.append(',');
        sb.append(this.position.getY());
        sb.append(',');
        sb.append(this.position.getZ());
        sb.append(',');
        sb.append(this.direction.name());
        sb.append(',');
        sb.append(this.getClass().toString());

        return sb.toString();
    }

    public void teleportToDestiny(Entity e) {
        if (this.linked != null) {
            e.teleport(this.linked.getTeleportLocation());
        }
    }

    protected Particle getParticle() {
        int pos = (this.isLeft ? 0 : 1); // left portal => pos 0
        return Portal.particles[pos];
    }

    public static void setParticle(Particle particle, boolean leftPortal) {
        int pos = (leftPortal ? 0 : 1); // left portal => pos 0
        Portal.particles[pos] = particle;
    }

    public abstract boolean insidePortal(Location loc);
    public abstract void playParticle();
    public abstract Location getTeleportLocation();
}

package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public abstract class Portal {
    private static HashMap<UUID, Portal[]> portals;
    private static HashMap<Location, Portal> portalsLocations;
    private static Particle[] particles;
    public static Function<Block, Boolean> isEmptyBlock;
    public static Function<Block, Boolean> isValidBlock;

    protected Portal linked;
    protected final Location position;
    protected final Direction direction; // TODO: direction implicid in Yaw
    protected final boolean isLeft; // used in the particle's color

    /* ABSTRACT FUNCTIONS */
    public abstract void playParticle();
    public abstract Location []calculateTeleportLocation();
    public abstract Location []calculateSupportLocation();

    static {
        Portal.portals = new HashMap<>();
        Portal.portalsLocations = new HashMap<>();
        Portal.particles = new Particle[2];
    }

    protected Portal(Location loc, Direction dir, boolean isLeft) {
        this.position = loc.clone();
        this.direction = dir;
        this.isLeft = isLeft;
        this.linked = null;
    }

    public Location getPosition() {
        return this.position.clone();
    }

    /**
     * @param loc location to check
     * @return index (teleportLocations[return] equals loc); -1 if not found
     */
    public short getLocationIndex(Location loc) {
        short x;
        Location []tp = this.calculateTeleportLocation();
        for (x = 0; x < tp.length; x++) {
            if (loc.equals(tp[x])) return x;
        }
        return -1;
    }

    public void setLinked(Portal l) {
        this.linked = l;
    }

    public static Portal getPortal(Location loc) {
        return portalsLocations.get(loc);
    }

    public static boolean existsPortal(Location loc) {
        return portalsLocations.containsKey(loc);
    }

    public boolean collides() {
        for (Location l : this.calculateTeleportLocation()) {
            if (Portal.existsPortal(l)) return true;
        }

        return false;
    }

    /**
     * @return true if the support locations are valid blocks and the teleport locations are empty blocks
     */
    public boolean isValid() {
        for(Location l : this.calculateSupportLocation()) {
            if (!Portal.isValidBlock.apply(l.getBlock())) return false;
        }
        for (Location l : this.calculateTeleportLocation()) {
            if (!Portal.isEmptyBlock.apply(l.getBlock())) return false;
        }

        return true;
    }

    public Direction getDirection() {
        return this.direction;
    }

    /**
     * @param id user's UUID
     * @param p new portal
     */
    public static void setPortal(UUID id, Portal p) {
        try {
            p = (Portal)p.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Portal[] userPortals = Portal.portals.get(id);
        int pos = (p.isLeft ? 0 : 1), otherPos = (!p.isLeft ? 0 : 1); // left portal => pos 0

        if (userPortals == null) {
            userPortals = new Portal[2];
            Portal.portals.put(id, userPortals);
        }
        else {
            // user have portals; the old one (if exists) must be eliminated
            Portal.removePortal(userPortals[pos]);
            if (userPortals[otherPos] != null) {
                userPortals[otherPos].setLinked(p);
                p.setLinked(userPortals[otherPos]);
            }
        }

        for (Location l: p.calculateTeleportLocation()) Portal.portalsLocations.put(l, p);
        userPortals[pos] = p;
    }

    /**
     * @param u user
     * @param p new portal
     */
    public static void setPortal(Player u, Portal p) {
        Portal.setPortal(u.getUniqueId(), p);
    }

    /**
     * @param p portal to be eliminated on HashMap (if exists)
     */
    public static void removePortal(Portal p) {
        if (p != null) {
            if (p.linked != null) p.linked.setLinked(null);
            for (Location l: p.calculateTeleportLocation()) Portal.portalsLocations.remove(l);
        }
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

    protected Particle getParticle() {
        int pos = (this.isLeft ? 0 : 1); // left portal => pos 0
        return Portal.particles[pos];
    }

    public static void setParticle(Particle particle, boolean leftPortal) {
        int pos = (leftPortal ? 0 : 1); // left portal => pos 0
        Portal.particles[pos] = particle;
    }


    /* TELEPORT */
    private static float getYaw(float playerYaw, Direction p1, Direction p2) {
        if (!Direction.aligned(p1, p2)) playerYaw += (p2.getValue() - p1.getValue());
        else if (p1 == p2) playerYaw += 180.f;

        return playerYaw % 360;
    }

    // TODO: Pitch
    private static float getPitch(float playerPitch, Portal in, Portal out) {
        return playerPitch;
    }

    public boolean teleportToDestiny(Entity e, short index) {
        if (this.linked != null) {
            Location []tp = this.linked.calculateTeleportLocation();
            if (index < 0 || index >= tp.length) index = 0;

            Location l = tp[index];
            l.add(0.5f, 0.f, 0.5f); // center
            l.setPitch(Portal.getPitch(e.getLocation().getPitch(), this, this.linked));
            l.setYaw(Portal.getYaw(e.getLocation().getYaw(), this.direction, this.linked.direction)); // Yaw

            // TODO: conserve velocity
            e.teleport(l);
            return true;
        }

        return false;
    }
}

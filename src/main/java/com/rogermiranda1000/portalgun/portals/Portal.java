package com.rogermiranda1000.portalgun.portals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.rogermiranda1000.portalgun.utils.raycast.Trajectory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import com.rogermiranda1000.portalgun.Direction;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.files.Config;
import com.rogermiranda1000.versioncontroller.particles.ParticleEntity;

public abstract class Portal {
    private static HashMap<UUID, Portal[]> portals;
    private static HashMap<Location, Portal> portalsLocations;
    private static ParticleEntity[] particles;
    public static Function<Block, Boolean> isEmptyBlock;
    public static Function<Block, Boolean> isValidBlock;
    protected static final int iterations = 20;

    protected Portal linked;
    protected final Location position;
    protected final Direction direction;
    protected final boolean isLeft; // used in the particle's color
    private short currentParticle;
    protected final OfflinePlayer owner;
    private final Location []particleLocations;

    /* ABSTRACT FUNCTIONS */
    public abstract Location getParticlePosition(short currentParticle);
    public abstract Vector getApproachVector();
    public abstract Location []calculateTeleportLocation();
    public abstract Location []calculateSupportLocation();

    static {
        Portal.portals = new HashMap<>();
        Portal.portalsLocations = new HashMap<>();
        Portal.particles = new ParticleEntity[2];
    }

    protected Portal(OfflinePlayer owner, Location loc, Direction dir, boolean isLeft) {
        this.owner = owner;
        this.position = loc.clone();
        this.direction = dir;
        this.isLeft = isLeft;
        this.linked = null;
        this.currentParticle = 0;
        this.particleLocations = this.calculateParticles();
    }

    public Location getPosition() {
        return this.position.clone();
    }

    public static ArrayList<Portal> getPortals() {
        ArrayList<Portal> r = new ArrayList<>();

        for (Portal[] portals : Portal.portals.values()) {
            for (short x = 0; x < portals.length; x++) {
                if (portals[x] != null) r.add(portals[x]);
            }
        }

        return r;
    }

    private Location[] calculateParticles() {
        Location []l = new Location[Portal.iterations];

        for (short x = 0; x < Portal.iterations; x++) {
            l[x] = this.getParticlePosition(x);
        }

        return l;
    }

    private static void spawnParticle(Location loc, ParticleEntity particle, Player owner) {
        if (loc == null) return;

        if(!Config.ONLY_YOUR_PORTALS.getBoolean()) particle.playParticle(loc.getWorld(), loc);
        else {
            for(Player ply: Bukkit.getOnlinePlayers()) {
                if(ply.hasPermission("portalgun.overrideotherportals") || ply.equals(owner)) particle.playParticle(ply, loc);
            }
        }
    }

    public void playParticle() {
        Player p = this.owner.getPlayer();

        Portal.spawnParticle(this.particleLocations[this.currentParticle], this.getParticle(), p);

        this.currentParticle += Portal.iterations/2;
        this.currentParticle %= Portal.iterations;

        Portal.spawnParticle(this.particleLocations[this.currentParticle], this.getParticle(), p);

        this.currentParticle++;
        this.currentParticle %= Portal.iterations;
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

    public Portal getLinked() {
        return this.linked;
    }

    public static Portal getPortal(Location loc) {
        return portalsLocations.get(loc);
    }

    /*public static boolean existsPortal(Location loc) {
        return portalsLocations.containsKey(loc);
    }

    public boolean collides() {
        for (Location l : this.calculateTeleportLocation()) {
            if (Portal.existsPortal(l)) return true;
        }

        return false;
    }*/

    public boolean collidesAndPersists() {
        for (Location l : this.calculateTeleportLocation()) {
            Portal p = Portal.getPortal(l);
            if (p != null && (this.getOwner() != p.getOwner() || this.getIsLeft() != p.getIsLeft())) return true;
        }

        return false;
    }

    @Nullable public Player getOwner() {
        return this.owner.getPlayer();
    }

    public boolean getIsLeft() {
        return this.isLeft;
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

            // TODO instead of searching the owner, add an attribute to the portal
            for (Map.Entry<UUID,Portal[]> usersPortals : Portal.portals.entrySet()) {
                int indexFound = -1;
                for (int n = 0; n < usersPortals.getValue().length && indexFound == -1; n++) {
                    if (p.equals(usersPortals.getValue()[n])) indexFound = n;
                }
                if (indexFound != -1) {
                    usersPortals.getValue()[indexFound] = null; // remove the portal from the user's list
                    break;
                }
            }
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

        sb.append(this.owner.getUniqueId());
        sb.append(';');
        sb.append(this.position.getWorld().getName());
        sb.append(',');
        sb.append(this.position.getX());
        sb.append(',');
        sb.append(this.position.getY());
        sb.append(',');
        sb.append(this.position.getZ());
        sb.append(';');
        sb.append(this.direction.name());
        sb.append(';');
        sb.append(this.isLeft ? 'L' : 'R');
        sb.append(';');
        sb.append(this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.') + 1));

        return sb.toString();
    }

    public static ParticleEntity getParticle(boolean isLeft) {
        int pos = (isLeft ? 0 : 1); // left portal => pos 0
        return Portal.particles[pos];
    }

    public ParticleEntity getParticle() {
        return Portal.getParticle(this.isLeft);
    }

    public static void setParticle(ParticleEntity particle, boolean leftPortal) {
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

    // TODO: teleport not to exact center block
    public boolean teleportToDestiny(final Entity e, Vector velocity, Location l) {
        if (this.linked == null || l == null) return false;

        l = l.clone();
        l.add(0.5f, 0.f, 0.5f); // center
        l.setPitch(Portal.getPitch(e.getLocation().getPitch(), this, this.linked)); // Pitch
        l.setYaw(Portal.getYaw(e.getLocation().getYaw(), this.direction, this.linked.direction)); // Yaw

        e.teleport(l);

        final Vector newVelocity = this.linked.getApproachVector().multiply( -velocity.dot(this.getApproachVector()) );
        Bukkit.getScheduler().runTaskLater(PortalGun.plugin, ()->e.setVelocity(newVelocity), 1L);

        return true;
    }

    public Trajectory getNewTrajectory(Trajectory in) {
        if (this.linked == null) return in;

        Location destiny = this.getDestiny(this.getLocationIndex(in.getLocation().getBlock().getLocation()));
        Vector direction = this.linked.direction.getVector();

        return new Trajectory(destiny, direction);
    }

    public boolean teleportToDestiny(Entity e, Vector velocity, short index) {
        return this.teleportToDestiny(e, velocity, getDestiny(index));
    }

    @Nullable
    public Location getDestiny(short index) {
        if (this.linked == null) return null;

        Location []tp = this.linked.calculateTeleportLocation();
        if (index < 0 || index >= tp.length) index = 0;

        return tp[index];
    }
}

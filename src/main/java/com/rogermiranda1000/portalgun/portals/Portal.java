package com.rogermiranda1000.portalgun.portals;

import java.util.*;
import java.util.function.Function;

import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.rogermiranda1000.helper.blocks.CustomBlock;
import com.rogermiranda1000.portalgun.utils.raycast.Trajectory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.rogermiranda1000.portalgun.Direction;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.files.Config;
import com.rogermiranda1000.versioncontroller.particles.ParticleEntity;

public abstract class Portal {
    private static HashMap<UUID, Portal[]> portals;
    private static RTree<Portal, Point> portalsLocations; // TODO instead of just the margins of each portal block, save the rectangle
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
        Portal.removeAllPortals(); // initialize
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

        if(!Config.getInstance().portals.useOnlyYours) particle.playParticle(loc.getWorld(), loc);
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

    public synchronized static Portal getPortal(Location loc) {
        Iterator<Entry<Portal,Point>> matches = Portal.portalsLocations.search(CustomBlock.getPointWithMargin(loc)).iterator();
        if (!matches.hasNext()) return null;
        return matches.next().value();
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
    public synchronized static void setPortal(UUID id, Portal p) {
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

        for (Location tp : p.calculateTeleportLocation()) Portal.portalsLocations = Portal.portalsLocations.add(p, CustomBlock.getPoint(tp));
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
    public synchronized static void removePortal(Portal p) {
        if (p != null) {
            if (p.linked != null) p.linked.setLinked(null);
            final List<Entry<Portal,Point>> toDelete = new ArrayList<>();
            Portal.portalsLocations.entries().forEach(e -> {
                if (e.value() == p) toDelete.add(e);
            });
            Portal.portalsLocations = Portal.portalsLocations.delete(toDelete);

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

    public synchronized static void removeAllPortals() {
        Portal.portals = new HashMap<>();
        Portal.portalsLocations = RTree.star().dimensions(5).create(); // MSB[world], LSB[world], x, y, z
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


    @NotNull
    public static Vector rotateAroundY(Vector v, double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);
        double x = angleCos * v.getX() + angleSin * v.getZ();
        double z = -angleSin * v.getX() + angleCos * v.getZ();
        return v.setX(x).setZ(z);
    }

    @NotNull
    public static Vector rotateAroundZ(Vector v, double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);
        double x = angleCos * v.getX() - angleSin * v.getY();
        double y = angleSin * v.getX() + angleCos * v.getY();
        return v.setX(x).setY(y);
    }

    // based on `Portal.getYaw`
    private static Vector getVector(Vector vector, Portal in, Portal out) {
        Vector inApproach = in.getApproachVector(),
                outApproach = out.getApproachVector();

        if (inApproach.clone().subtract(outApproach).length() <= CustomBlock.EPSILON) vector = vector.multiply(-1); // same vector
        else if (inApproach.clone().multiply(-1).subtract(outApproach).length() > CustomBlock.EPSILON) { // not equals and not opposite
            double deltaTheta = -(out.direction.getValue() - in.direction.getValue()),
                    deltaPhi = (outApproach.getY() - inApproach.getY()) * 90.f;
            vector = rotateAroundZ(vector, Math.toRadians(deltaPhi));
            vector = rotateAroundY(vector, Math.toRadians(deltaTheta));
        }
        return vector;
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
        // This function is the result of many try-error combinations, feel free to make it more understandable.

        if (this.linked == null) return in;
        if (in.getDirection().normalize().dot(this.getApproachVector()) <= 0.f) return in; // not approaching (extracted from `onMove`)

        Vector destinyOffset = Portal.getVector(this.particleLocations[0].toVector().subtract(in.getLocation().toVector()),
                this, this.linked);
        if (!this.linked.direction.equals(this.direction)) {
            // for some reason is 1 block offset
            // TODO some top/bottom portal are odd
            destinyOffset = destinyOffset.add(Portal.getVector(new Vector(0,-1,0), this, this.linked));
        }
        Location destiny = this.linked.particleLocations[0].clone().add(destinyOffset);

        Vector direction = Portal.getVector(in.getDirection(), this, this.linked);

        return new Trajectory(destiny, direction);
    }

    public boolean teleportToDestiny(Entity e, Vector velocity, short index) {
        return this.teleportToDestiny(e, velocity, getDestiny(index));
    }

    @Nullable
    public Location getDestiny(short index) {
        if (this.linked == null) return null;

        Location []tp = this.linked.calculateTeleportLocation();
        if (index < 0) index = 0;
        index %= tp.length;

        return tp[index];
    }
}

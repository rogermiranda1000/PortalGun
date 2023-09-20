package com.rogermiranda1000.portalgun.blocks.beam;

import com.rogermiranda1000.portalgun.blocks.ThermalBeams;
import com.rogermiranda1000.portalgun.blocks.ThermalReceivers;
import com.rogermiranda1000.portalgun.cubes.Cubes;
import com.rogermiranda1000.portalgun.cubes.RedirectionCube;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.utils.raycast.Trajectory;
import com.rogermiranda1000.versioncontroller.particles.ParticleEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Beam {
    public static class BeamStep {
        private final Location loc;
        private final Vector dir;

        public BeamStep(Location loc, Vector dir) {
            this.loc = loc;
            this.dir = dir;
        }

        public Location getLocation() {
            return this.loc.clone();
        }

        public Vector getDirection() {
            return this.dir.clone();
        }

        @Override
        public boolean equals(Object o) {
            return this.equals(o, 0);
        }

        public boolean equals(Object o, float marginRadius) {
            if (o == this) return true;
            if (!(o instanceof BeamStep)) return false;

            BeamStep that = (BeamStep)o;
            Vector l1 = this.getLocation().add(this.getDirection()).toVector(),
                    l2 = that.getLocation().add(that.getDirection()).toVector();
            return (l1.subtract(l2).length() <= marginRadius);
        }
    }
    public static ParticleEntity LASER_PARTICLE;
    public static float MAX_DISTANCE = 45.f;
    public static final float STEP_SIZE = 0.3f;
    private static final int ITERATIONS_PER_TICK = 10;

    private final Location originLocation;
    private final Vector originDirection;
    private final BeamDisruptedEvent onBeamDisrupted;

    private int validationTrailIndex;
    private List<BeamStep> trails;

    public Beam(Location origin, Vector direction, BeamDisruptedEvent callback) {
        this.originLocation = origin;
        this.originDirection = direction;
        this.onBeamDisrupted = callback;

        this.trails = new ArrayList<>();
        this.validationTrailIndex = 1;
    }

    private static int getMaxTrailElementsUntilMaxSize() {
        return (int)Math.ceil(MAX_DISTANCE / STEP_SIZE);
    }

    @Nullable
    private static BeamStep calculateNext(BeamStep last, @Nullable BeamDisruptedEvent callback) {
        Location nextLocation = last.getLocation().add(last.getDirection().multiply(STEP_SIZE));
        Vector nextDirection = last.getDirection();

        // TODO check for portal collision

        Block nextLocationBlock = nextLocation.getBlock();
        if (!Portal.isEmptyBlock.apply(nextLocationBlock)) {
            // colliding with a block
            Portal p;
            // were there a portal before collide?
            if ((p = Portal.getPortal(last.getLocation().getBlock().getLocation())) != null) {
                // colliding with a portal
                Trajectory in = new Trajectory(last.getLocation(), nextDirection);
                Trajectory trajectory = p.getNewTrajectory(in);
                if (trajectory == in) p = null; // not really opened
                else {
                    nextLocation = trajectory.getLocation();
                    nextDirection = trajectory.getDirection();
                }
            }
            if (p == null) {
                // no portal; stop
                if (callback != null) callback.onBeamDisrupted(nextLocationBlock, nextLocation);
                return null;
            }
        }

        // TODO we may have synchronization problems, as ThermalReceiver swaps decorates and this is being run on a different thread
        Entity collidingWith = nextLocation.getWorld().getNearbyEntities(nextLocation, 0.25f, 1f, 0.25f)
                .stream().filter(e -> !ThermalReceivers.getInstance().isDecorate(e) && !ThermalBeams.getInstance().isDecorate(e)).findFirst().orElse(null);
        RedirectionCube redirectionCube = null;
        if (collidingWith != null && (redirectionCube = Cubes.getCube(collidingWith, RedirectionCube.class)) == null) {
            if (callback != null) callback.onBeamDisrupted(collidingWith, nextLocation);
            return null;
        }
        if (redirectionCube != null) {
            // hitted by a redirection cube
            double distance = nextLocation.clone().subtract(redirectionCube.getCubeCenter()).toVector()
                                    .length();
            if (distance < 0.3f) { // it needs to be in the center (otherwise it will get redirected at the beginning)
                float yaw = collidingWith.getLocation().getYaw();
                nextDirection = Beam.yawToVector(yaw);
            }
        }

        return new BeamStep(nextLocation, nextDirection);
    }

    public void tick() {
        for (int iteration = 0; iteration < ITERATIONS_PER_TICK; iteration++) {
            // validate old beams
            if (this.validationTrailIndex >= this.trails.size()) this.validationTrailIndex = 1;
            else {
                BeamStep lastChecked = this.trails.get(this.validationTrailIndex-1),
                        currentlyChecking = this.trails.get(this.validationTrailIndex);
                BeamStep next = Beam.calculateNext(lastChecked, null);

                if (!currentlyChecking.equals(next, 0.001f)) {
                    // the trail has changed; remove all elements from this point to the last
                    this.trails = this.trails.subList(0, this.validationTrailIndex-1);
                }

                this.validationTrailIndex++;
            }


            // increase beam
            if (this.trails.isEmpty()) {
                this.trails.add(new BeamStep(this.originLocation, this.originDirection));
            }
            else {
                BeamStep last = this.trails.get(this.trails.size()-1);
                BeamStep next = Beam.calculateNext(last, this.onBeamDisrupted);
                if (next != null) {
                    if (this.trails.size() < getMaxTrailElementsUntilMaxSize()) this.trails.add(next);
                    else this.onBeamDisrupted.onBeamDisrupted(next.getLocation().getBlock(), next.getLocation());
                }
            }
        }
    }

    public static Vector yawToVector(float yaw) {
        // A value of 0 means south, 90 west, ±180 north, and -90 east.
        // Convert yaw to radians
        double yawRadians = Math.toRadians(yaw);

        // Calculate the X and Z components of the vector using trigonometric functions
        double x = -Math.sin(yawRadians);
        double z = Math.cos(yawRadians);

        return new Vector(x,0,z);
    }

    public static float vectorToYaw(Vector v) {
        // Calculate the yaw angle in radians
        double yawRadians = Math.atan2(-v.getX(), v.getZ());

        // Convert radians to degrees and adjust for Minecraft's yaw convention
        float yaw = (float) Math.toDegrees(yawRadians);

        // Ensure the yaw value is within the range of -180 to 180
        while (yaw > 180) {
            yaw -= 360;
        }
        while (yaw <= -180) {
            yaw += 360;
        }

        return yaw;
    }

    public void playParticles() {
        if (LASER_PARTICLE == null) return;

        // TODO alter particles instead of playing them all
        for (BeamStep step : this.trails) {
            LASER_PARTICLE.playParticle(step.getLocation().getWorld(), step.getLocation());
        }
    }
}

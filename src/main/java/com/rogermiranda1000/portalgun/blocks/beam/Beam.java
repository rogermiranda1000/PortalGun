package com.rogermiranda1000.portalgun.blocks.beam;

import com.rogermiranda1000.portalgun.cubes.Cubes;
import com.rogermiranda1000.portalgun.cubes.RedirectionCube;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.versioncontroller.particles.ParticleEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

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
    }
    public static ParticleEntity LASER_PARTICLE;
    public static float MAX_DISTANCE = 45.f;
    public static final float STEP_SIZE = 0.3f;
    private static final int ITERATIONS_PER_TICK = 5;

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
        this.validationTrailIndex = 0;
    }

    private static int getMaxTrailElementsUntilMaxSize() {
        return (int)Math.ceil(MAX_DISTANCE / STEP_SIZE);
    }

    public void tick() {
        for (int iteration = 0; iteration < ITERATIONS_PER_TICK; iteration++) {
            // validate old beams
            if (this.validationTrailIndex >= this.trails.size()) this.validationTrailIndex = 0;
            else {

            }


            // increase beam
            if (this.trails.size() == 0) {
                this.trails.add(new BeamStep(this.originLocation, this.originDirection));
            }
            else {
                BeamStep last = this.trails.get(this.trails.size()-1);
                Location nextLocation = last.getLocation().add(last.getDirection().multiply(STEP_SIZE));
                Vector nextDirection = last.getDirection();

                Block nextLocationBlock = nextLocation.getBlock();
                if (!Portal.isEmptyBlock.apply(nextLocationBlock) || this.trails.size() >= getMaxTrailElementsUntilMaxSize()) {
                    this.onBeamDisrupted.onBeamDisrupted(nextLocationBlock);
                    return;
                }

                Entity collidingWith = this.originLocation.getWorld().getNearbyEntities(this.originLocation, 0.25f, 1f, 0.25f)
                                                .stream().findFirst().orElse(null);
                RedirectionCube redirectionCube = null;
                if (collidingWith != null && (redirectionCube = Cubes.getCube(collidingWith, RedirectionCube.class)) == null) {
                    this.onBeamDisrupted.onBeamDisrupted(collidingWith);
                    return;
                }
                if (redirectionCube != null) {
                    // hitted by a redirection cube
                    float yaw = collidingWith.getLocation().getYaw();
                    nextDirection = Beam.yawToVector(yaw);
                }

                BeamStep next = new BeamStep(nextLocation, nextDirection);
                this.trails.add(next);
            }
        }
    }

    private static Vector yawToVector(float yaw) {
        // A value of 0 means south, 90 west, ±180 north, and -90 east.
        // Convert yaw to radians
        double yawRadians = Math.toRadians(yaw);

        // Calculate the X and Z components of the vector using trigonometric functions
        double x = -Math.sin(yawRadians);
        double z = Math.cos(yawRadians);

        return new Vector(x,0,z);
    }

    public void playParticles() {
        if (LASER_PARTICLE == null) return;

        // TODO alter particles instead of playing them all
        for (BeamStep step : this.trails) {
            LASER_PARTICLE.playParticle(step.getLocation().getWorld(), step.getLocation());
        }
    }
}

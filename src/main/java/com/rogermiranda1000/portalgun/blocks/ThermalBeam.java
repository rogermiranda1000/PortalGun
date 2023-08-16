package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.portalgun.blocks.beam.Beam;
import com.rogermiranda1000.portalgun.blocks.beam.BeamDisruptedEvent;
import com.rogermiranda1000.portalgun.blocks.beam.CachedBeamDisruptedEvent;
import com.rogermiranda1000.portalgun.cubes.Cubes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class ThermalBeam implements BeamDisruptedEvent {

    private final Location location;
    private final Vector direction;
    private final Beam beam;
    @Nullable public ThermalReceiver powering;

    public ThermalBeam(Location location, Vector direction) {
        this.location = location;
        this.direction = direction;

        this.beam = new Beam(new Location(
                this.location.getWorld(),
                this.location.getBlockX() + 0.5f + direction.getX()/2,
                this.location.getBlockY() + 0.5f,
                this.location.getBlockZ() + 0.5f + direction.getZ()/2
        ), this.direction, new CachedBeamDisruptedEvent(this));
    }

    @Override
    public void onBeamDisrupted(Entity e, Location hit) {
        this.power(null); // de-power (if any being powered)
        if (!Cubes.isCube(e)) {
            // non-cube entity disrupted the laser; burn it
            e.setFireTicks(20);
        }
    }

    @Override
    public void onBeamDisrupted(Block b, Location hit) {
        ThermalReceiver receiver = ThermalReceivers.getInstance().getBlock(b.getLocation());
        if (receiver == null) {
            // not a thermal receiver
            this.power(null); // just in case they were powering something
            return;
        }

        // end vector - (center of the block)
        Vector hitVector = hit.toVector().subtract(receiver.getPosition().toVector()).subtract(new Vector(0.5f,0.5f,0.5f));
        if (hitVector.length() > 0 && hitVector.normalize()/*.multiply(-1)*/
                                            .dot(receiver.getDirection()) < Math.cos(Math.toRadians(45))) { // for some reason, checking if the angle is < and I don't multiply by -1, it makes it check if it is >
            // not the right face of the receiver
            this.power(null); // just in case they were powering something
            return;
        }

        this.power(receiver);
    }

    public void power(@Nullable ThermalReceiver receiver) {
        if (this.powering != null) this.powering.unpower();

        this.powering = receiver;
        if (this.powering != null) this.powering.power();
    }

    public void playParticles() {
        this.beam.tick();
        this.beam.playParticles();
    }

    public Location getPosition() {
        return this.location;
    }
}

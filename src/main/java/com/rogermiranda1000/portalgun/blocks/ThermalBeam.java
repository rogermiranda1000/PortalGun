package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.versioncontroller.particles.ParticleEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class ThermalBeam {
    public static float MAX_DISTANCE = 45.f;
    public static ParticleEntity LASER_PARTICLE;

    public final Location location;
    public final Vector direction;
    public final Location particlesOrigin;
    @Nullable public ThermalReceiver powering;

    public ThermalBeam(Location location, Vector direction) {
        this.location = location;
        this.direction = direction;

        this.particlesOrigin = new Location(
                this.location.getWorld(),
                this.location.getBlockX() + 0.5f + direction.getX()/2,
                this.location.getBlockY() + 0.5f,
                this.location.getBlockZ() + 0.5f + direction.getX()/2
        );
    }

    public void power(@Nullable ThermalReceiver receiver) {
        if (this.powering != null) this.powering.unpower();

        this.powering = receiver;
        if (this.powering != null) this.powering.power();
    }

    public void playParticles() {
        Bukkit.getLogger().info(this.direction.toString());
        if (LASER_PARTICLE == null) return;

        Location loc = this.particlesOrigin.clone().add(this.direction);
        LASER_PARTICLE.playParticle(loc.getWorld(), loc);

        // TODO check if powering
    }

    public Location getPosition() {
        return this.location;
    }
}

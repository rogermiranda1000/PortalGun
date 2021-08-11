package com.rogermiranda1000.versioncontroller.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * ParticleManager for version >= 1.9
 */
public class ParticlePost9 implements ParticleManager {
    @Override
    public void playParticle(World world, Object particle, Location loc) {
        world.spawnParticle((Particle) particle, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void playParticle(Player ply, Object particle, Location loc) {
        ply.spawnParticle((Particle) particle, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public Object getParticle(String particle) throws IllegalArgumentException {
        return Particle.valueOf(particle);
    }
}

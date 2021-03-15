package com.rogermiranda1000.versioncontroller.particles;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * ParticleManager for version < 1.9
 */
public class ParticlePre9 implements ParticleManager {
    @Override
    public void playParticle(World world, Object particle, Location loc) {
        world.playEffect(loc, (Effect) particle, 0);
    }

    @Override
    public void playParticle(Player ply, Object particle, Location loc) {
        ply.playEffect(loc, (Effect) particle, 0);
    }

    @Override
    public Object getParticle(String particle) throws IllegalArgumentException {
        return Effect.valueOf(particle);
    }
}

package com.rogermiranda1000.versioncontroller.particles;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface ParticleManager {
    /**
     * Plays a particle
     * @param world Location's world
     * @param particle Particle
     * @param loc Particle's location
     */
    void playParticle(World world, Object particle, Location loc);

    /**
     * Plays a particle to only one player
     * @param ply Player who the particle will be shown
     * @param particle Particle to show
     * @param loc Location to show the particle
     */
    void playParticle(Player ply, Object particle, Location loc);

    /**
     * Get the particle
     * @param particle Particle's name
     * @return Particle
     * @throws IllegalArgumentException Particle not found
     */
    Object getParticle(String particle) throws IllegalArgumentException;
}

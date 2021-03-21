package com.rogermiranda1000.versioncontroller.particles;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ParticleManager for version < 1.9
 */
public class ParticlePre9 implements ParticleManager {
    private static final int RADIUS = 35;
    @Nullable
    private static final Method playWorldEffectMethod = ParticlePre9.getPlayWorldEffectMethod();
    @Nullable
    private static final Method playPlayerEffectMethod = ParticlePre9.getPlayPlayerEffectMethod();

    @Nullable
    private static Method getPlayPlayerEffectMethod() {
        try {
            return Player.Spigot.class.getMethod("playEffect", Location.class, Effect.class, int.class, int.class, float.class, float.class, float.class, float.class, int.class, int.class);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static Method getPlayWorldEffectMethod() {
        try {
            return World.Spigot.class.getMethod("playEffect", Location.class, Effect.class, int.class, int.class, float.class, float.class, float.class, float.class, int.class, int.class);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("ConstantConditions") // ignore NPE
    @Override
    public void playParticle(World world, Object particle, Location loc) {
        try {
            ParticlePre9.playWorldEffectMethod.invoke(world.spigot(), loc, (Effect) particle, 0, 0, 0.f, 0.f, 0.f, 0.f, 1, ParticlePre9.RADIUS);
        } catch (IllegalAccessException | NullPointerException | InvocationTargetException e) {
            //e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions") // ignore NPE
    @Override
    public void playParticle(Player ply, Object particle, Location loc) {
        try {
            ParticlePre9.playPlayerEffectMethod.invoke(ply.spigot(), loc, (Effect) particle, 0, 0, 0.f, 0.f, 0.f, 0.f, 1, ParticlePre9.RADIUS);
        } catch (IllegalAccessException | NullPointerException | InvocationTargetException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public Object getParticle(String particle) throws IllegalArgumentException {
        return Effect.valueOf(particle);
    }
}

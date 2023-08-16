package com.rogermiranda1000.portalgun.cubes;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.events.onUse;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cubes implements Listener {
    private static final HashMap<Location,ArrayList<Cube>> oldCubes = new HashMap<>();
    private static final ArrayList<Cube> cubes = new ArrayList<>();

    public static boolean isCube(final @NotNull Entity e) {
        return Cubes.getCube(e) != null;
    }

    public static <T extends Cube> boolean isCube(final @NotNull Entity e, Class<T> cls) {
        try {
            return Cubes.getCube(e, cls) != null;
        } catch (ClassCastException ex) {
            return false; // is cube, but not from this type
        }
    }

    public static boolean isCubeSkeleton(final @NotNull Entity e) {
        if (!e.getType().equals(EntityType.ARMOR_STAND)) return false;

        synchronized (Cubes.cubes) {
            return Cubes.cubes.stream().anyMatch(c -> c.isSkeleton(e));
        }
    }

    @Nullable
    public static Cube getCube(final @NotNull Entity e) {
        if (!e.getType().equals(EntityType.ARMOR_STAND)) return null;

        synchronized (Cubes.cubes) {
            return Cubes.cubes.stream().filter(c -> c.isCube(e)).findFirst().orElse(null);
        }
    }

    @Nullable
    public static <T extends Cube> T getCube(final @NotNull Entity e, Class<T> cls) throws ClassCastException {
        return cls.cast(Cubes.getCube(e));
    }

    public static void spawnCube(Cube c, boolean removeOld) {
        Location loc = c.getSpawnLocation();
        Cube cube = c.spawn();
        synchronized (Cubes.cubes) {
            Cubes.cubes.add(cube);

            // remove old (if desired)
            if (removeOld) {
                ArrayList<Cube> olds = Cubes.oldCubes.get(loc);
                if (olds != null) {
                    olds = new ArrayList<>(olds);
                    for (Cube oldCube : olds) {
                        if (oldCube.getClass().equals(c.getClass())) {
                            // same spawn location, and same block type; destroy
                            Cubes.destroyCompanionCube(oldCube);
                        }
                    }
                    Cubes.oldCubes.remove(loc);
                }
            }

            // add to remove cache
            ArrayList<Cube> olds = Cubes.oldCubes.get(loc);
            if (olds == null) {
                olds = new ArrayList<>();
                Cubes.oldCubes.put(loc, olds);
            }
            olds.add(cube);
        }
    }

    public static void destroyCompanionCube(Cube e) {
        synchronized (Cubes.cubes) {
            Cubes.cubes.remove(e);
        }
        e.remove();
        // TODO play sound
    }

    public static void clear() {
        synchronized (Cubes.cubes) {
            List<Cube> toDelete = new ArrayList<>(Cubes.cubes);
            for (Cube e : toDelete) Cubes.destroyCompanionCube(e);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent e) {
        if (!Cubes.isCube(e.getRightClicked())) return;

        e.setCancelled(true);
        // simulate a right click
        PortalGun.plugin.getListener(onUse.class).onPlayerUse(new PlayerInteractEvent(
                e.getPlayer(),
                Action.RIGHT_CLICK_AIR,
                null,null,null // not used
        ));
    }

    // <1.9 doesn't have ArmorStand#setInvulnerable
    @EventHandler
    public void onEntityDamages(EntityDamageEvent e) {
        if (!Cubes.isCube(e.getEntity())) return;

        e.setCancelled(true);
    }

    public static void updateCompanionCubes() {
        List<Cube> cubes;
        synchronized (Cubes.cubes) {
            cubes = new ArrayList<>(Cubes.cubes);
        }
        for (Cube cube : cubes) cube.tick();
    }
}

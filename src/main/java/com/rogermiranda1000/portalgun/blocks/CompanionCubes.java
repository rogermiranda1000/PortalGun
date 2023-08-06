package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.events.onPortalgunEntity;
import com.rogermiranda1000.portalgun.events.onUse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompanionCubes implements Listener {
    private static final HashMap<Location,ArrayList<CompanionCube>> oldCubes = new HashMap<>();
    private static final ArrayList<CompanionCube> companionCubes = new ArrayList<>();

    public static boolean isCompanionCube(final @NotNull Entity e) {
        return CompanionCubes.getCompanionCube(e) != null;
    }

    public static boolean isCompanionCubeSkeleton(final @NotNull Entity e) {
        if (!e.getType().equals(EntityType.ARMOR_STAND)) return false;

        synchronized (CompanionCubes.companionCubes) {
            return CompanionCubes.companionCubes.stream().anyMatch(c -> c.isCompanionCubeSkeleton(e));
        }
    }

    @Nullable
    public static CompanionCube getCompanionCube(final @NotNull Entity e) {
        if (!e.getType().equals(EntityType.ARMOR_STAND)) return null;

        synchronized (CompanionCubes.companionCubes) {
            return CompanionCubes.companionCubes.stream().filter(c -> c.isCompanionCube(e)).findFirst().orElse(null);
        }
    }

    public static void spawnCompanionCube(Location loc, boolean removeOld) {
        CompanionCube cube = new CompanionCube(loc).spawn();
        synchronized (CompanionCubes.companionCubes) {
            CompanionCubes.companionCubes.add(cube);

            // remove old (if desired)
            if (removeOld) {
                ArrayList<CompanionCube> olds = oldCubes.remove(loc);
                if (olds != null) {
                    for (CompanionCube e : olds) CompanionCubes.destroyCompanionCube(e);
                }
            }

            // add to remove cache
            ArrayList<CompanionCube> olds = oldCubes.get(loc);
            if (olds == null) {
                olds = new ArrayList<>();
                oldCubes.put(loc, olds);
            }
            olds.add(cube);
        }
    }

    public static void destroyCompanionCube(CompanionCube e) {
        synchronized (CompanionCubes.companionCubes) {
            CompanionCubes.companionCubes.remove(e);
        }
        e.remove();
        // TODO play sound
    }

    public static void clear() {
        synchronized (CompanionCubes.companionCubes) {
            List<CompanionCube> toDelete = new ArrayList<>(CompanionCubes.companionCubes);
            for (CompanionCube e : toDelete) CompanionCubes.destroyCompanionCube(e);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent e) {
        if (!CompanionCubes.isCompanionCube(e.getRightClicked())) return;

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
        if (!CompanionCubes.isCompanionCube(e.getEntity())) return;

        e.setCancelled(true);
        /*if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (!(event.getDamager() instanceof Player)) return;
            Player player = ((Player) event.getDamager());

            // simulate a left click
            PortalGun.plugin.getListener(onUse.class).onPlayerUse(new PlayerInteractEvent(
                    player,
                    Action.LEFT_CLICK_AIR,
                    null, null, null // not used
            ));
        }*/
    }

    public static void updateCompanionCubes() {
        List<CompanionCube> cubes;
        synchronized (CompanionCubes.companionCubes) {
            cubes = new ArrayList<>(CompanionCubes.companionCubes);
        }
        for (CompanionCube cube : cubes) cube.tick();
    }
}

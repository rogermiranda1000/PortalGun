package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.portalgun.PortalGun;
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

import java.util.ArrayList;
import java.util.HashMap;

public class CompanionCubes implements Listener {
    private static final HashMap<Location,ArrayList<Entity>> oldCubes = new HashMap<>();
    private static final ArrayList<Entity> companionCubes = new ArrayList<>();

    public static boolean isCompanionCube(@NotNull Entity e) {
        if (!e.getType().equals(EntityType.ARMOR_STAND)) return false;

        synchronized (CompanionCubes.companionCubes) {
            return CompanionCubes.companionCubes.contains(e);
        }
    }

    public static void spawnCompanionCube(Location loc, boolean removeOld) {
        ArmorStand cube = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        synchronized (CompanionCubes.companionCubes) {
            CompanionCubes.companionCubes.add(cube);

            // remove old (if desired)
            if (removeOld) {
                ArrayList<Entity> olds = oldCubes.remove(loc);
                if (olds != null) {
                    for (Entity e : olds) CompanionCubes.destroyCompanionCube(e);
                }
            }

            // add to remove cache
            ArrayList<Entity> olds = oldCubes.get(loc);
            if (olds == null) {
                olds = new ArrayList<>();
                oldCubes.put(loc, olds);
            }
            olds.add(cube);
        }

        cube.setVisible(false);
        cube.setHelmet(new ItemStack(Material.IRON_BLOCK)); // TODO custom texture
    }

    public static void destroyCompanionCube(Entity e) {
        synchronized (CompanionCubes.companionCubes) {
            CompanionCubes.companionCubes.remove(e);
        }
        e.remove(); // TODO check if isAlive?
        // TODO play sound
    }

    public static void clear() {
        synchronized (CompanionCubes.companionCubes) {
            for (Entity e : CompanionCubes.companionCubes) CompanionCubes.destroyCompanionCube(e);
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
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (!(event.getDamager() instanceof Player)) return;

            // simulate a left click
            PortalGun.plugin.getListener(onUse.class).onPlayerUse(new PlayerInteractEvent(
                    (Player)event.getDamager(),
                    Action.LEFT_CLICK_AIR,
                    null, null, null // not used
            ));
        }
    }
}

package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanionCubes implements Listener {
    private static final HashMap<Location,ArrayList<Entity>> oldCubes = new HashMap<>();
    private static final ArrayList<Entity> companionCubes = new ArrayList<>();

    public static boolean isCompanionCube(Entity e) {
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

    @EventHandler
    public void onArmorStandInteract(PlayerArmorStandManipulateEvent e) {
        if (!CompanionCubes.isCompanionCube(e.getRightClicked())) return;

        // TODO would ArmorStand#addEquipmentLock do the trick?
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamages(EntityDamageEvent e) {
        if (!CompanionCubes.isCompanionCube(e.getEntity())) return;

        e.setCancelled(true);
    }
}

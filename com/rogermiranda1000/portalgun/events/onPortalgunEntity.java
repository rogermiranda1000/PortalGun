package com.rogermiranda1000.portalgun.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class onPortalgunEntity {
    public static final Set<Class<? extends Entity>> entityPickBlacklist = new HashSet<>();
    private static final HashMap<Player, Entity> pickedEntities = new HashMap<>();
    private static final float LAUNCH_VELOCITY_MULTIPLIER = 1.5f;

    public void onEntityPick(PlayerPickEvent event) {
        if (entityPickBlacklist.contains(event.getEntityPicked().getClass())) {
            event.setCancelled(true);
            return;
        }

        // TODO set gravity
        //event.getEntityPicked().setGravity(false);
        pickedEntities.put(event.getPlayer(), event.getEntityPicked());
    }

    public void launchEntity(Player p) {
        Entity e = pickedEntities.remove(p);
        //e.setGravity(true);
        e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).multiply(LAUNCH_VELOCITY_MULTIPLIER));
    }

    // TODO free on item change from hand
    public void freeEntity(Player p) {
        Entity e = pickedEntities.remove(p);
        //e.setGravity(true);
    }

    public static boolean haveEntityPicked(Player p) {
        return (getEntityPicked(p) != null);
    }

    @Nullable
    public static Entity getEntityPicked(Player p) {
        return pickedEntities.get(p);
    }

    public static boolean isEntityPicked(Entity e) {
        return pickedEntities.containsValue(e);
    }

    public static void updatePickedEntities() {
        for (Map.Entry<Player,Entity> e : pickedEntities.entrySet()) {
            e.getValue().teleport(e.getKey().getLocation().clone().add(0,1,1));
        }
    }
}

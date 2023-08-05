package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.blocks.CompanionCubes;
import com.rogermiranda1000.portalgun.utils.raycast.Ray;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class onPortalgunEntity {
    public static final Set<String> entityPickBlacklist = new HashSet<>();
    private static final HashMap<Player, Entity> pickedEntitiesAndPicker = new HashMap<>();
    private static final float LAUNCH_VELOCITY_MULTIPLIER = 1.f,
                            PICKED_ENTITY_DISTANCE = 2.5f;

    public void onEntityPick(PlayerPickEvent event) {
        String entityPickedName = event.getEntityPicked().getType().name().toLowerCase();
        if (CompanionCubes.isCompanionCube(event.getEntityPicked())) entityPickedName = "COMPANION_CUBE";

        if (entityPickBlacklist.contains(entityPickedName)) {
            event.setCancelled(true);
            return;
        }

        if (VersionController.version.compareTo(Version.MC_1_10) >= 0) event.getEntityPicked().setGravity(false);
        synchronized (pickedEntitiesAndPicker) {
            pickedEntitiesAndPicker.put(event.getPlayer(), event.getEntityPicked());
        }
        // TODO sound
    }

    public void launchEntity(Player p) {
        Entity e;
        synchronized (pickedEntitiesAndPicker) {
            e = pickedEntitiesAndPicker.remove(p);
        }
        if (VersionController.version.compareTo(Version.MC_1_10) >= 0) e.setGravity(true);
        e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).multiply(LAUNCH_VELOCITY_MULTIPLIER));
        // TODO sound
    }

    // TODO free on item change from hand
    public void freeEntity(Player p) {
        onPortalgunEntity.removeEntity(p);
    }

    public static boolean haveEntityPicked(Player p) {
        return (getEntityPicked(p) != null);
    }

    @Nullable
    public static Entity getEntityPicked(Player p) {
        synchronized (pickedEntitiesAndPicker) {
            return pickedEntitiesAndPicker.get(p);
        }
    }

    public static boolean isEntityPicked(Entity e) {
        synchronized (pickedEntitiesAndPicker) {
            return pickedEntitiesAndPicker.containsValue(e);
        }
    }

    public static void removeEntity(Player p) {
        Entity e;
        synchronized (pickedEntitiesAndPicker) {
            e = pickedEntitiesAndPicker.remove(p);
        }
        if (e == null) return;
        if (VersionController.version.compareTo(Version.MC_1_10) >= 0) e.setGravity(true);
    }

    public static void clear() {
        synchronized (pickedEntitiesAndPicker) {
            for (Player p : pickedEntitiesAndPicker.keySet()) removeEntity(p);
        }
    }

    /**
     * Get the maximun location the Entity can be moved to the destiny without colliding with any solid block
     * @param e         Entity to teleport
     * @param destiny   Target destiny
     * @return          Teleported
     */
    private static Location secureTeleport(Entity e, Location destiny) {
        Location start = e.getLocation(), pre = start, current = start;
        if (!start.getWorld().equals(destiny.getWorld())) return start;

        // @author https://www.geeksforgeeks.org/bresenhams-algorithm-for-3-d-line-drawing/
        if (!VersionController.get().isPassable(start.getBlock())) return start;
        int dx = Math.abs(destiny.getBlockX() - start.getBlockX()),
                dy = Math.abs(destiny.getBlockY() - start.getBlockY()),
                dz = Math.abs(destiny.getBlockZ() - start.getBlockZ());
        int xs = (destiny.getBlockX() > start.getBlockX()) ? 1 : -1,
            ys = (destiny.getBlockY() > start.getBlockY()) ? 1 : -1,
            zs = (destiny.getBlockZ() > start.getBlockZ()) ? 1 : -1;
        int p1, p2;

        if (dx == 0 && dy == 0 && dz == 0) return destiny;
        if (dx >= dy && dx >= dz) {
            p1 = 2*dy - dx;
            p2 = 2*dz - dx;
            while (current.getBlockX() != destiny.getBlockX()) {
                current = new Location(current.getWorld(), current.getBlockX()+xs, current.getBlockY(), current.getBlockZ());
                if (p1 >= 0) {
                    current = new Location(current.getWorld(), current.getBlockX(), current.getBlockY()+ys, current.getBlockZ());
                    p1 -= 2*dx;
                }
                if (p2 >= 0) {
                    current = new Location(current.getWorld(), current.getBlockX(), current.getBlockY(), current.getBlockZ()+zs);
                    p2 -= 2*dx;
                }

                p1 += 2*dy;
                p2 += 2*dz;
                if (!VersionController.get().isPassable(current.getBlock())) return pre;
                pre = current;
            }
        }
        else if (dy >= dx && dy >= dz) {
            p1 = 2*dx - dy;
            p2 = 2*dz - dy;
            while (current.getBlockY() != destiny.getBlockY()) {
                current = new Location(current.getWorld(), current.getBlockX(), current.getBlockY()+ys, current.getBlockZ());
                if (p1 >= 0) {
                    current = new Location(current.getWorld(), current.getBlockX()+xs, current.getBlockY(), current.getBlockZ());
                    p1 -= 2*dy;
                }
                if (p2 >= 0) {
                    current = new Location(current.getWorld(), current.getBlockX(), current.getBlockY(), current.getBlockZ()+zs);
                    p2 -= 2*dy;
                }

                p1 += 2*dx;
                p2 += 2*dz;
                if (!VersionController.get().isPassable(current.getBlock())) return pre;
                pre = current;
            }
        }
        else {
            p1 = 2*dy - dz;
            p2 = 2*dx - dz;
            while (current.getBlockZ() != destiny.getBlockZ()) {
                current = new Location(current.getWorld(), current.getBlockX(), current.getBlockY(), current.getBlockZ()+zs);
                if (p1 >= 0) {
                    current = new Location(current.getWorld(), current.getBlockX(), current.getBlockY()+ys, current.getBlockZ());
                    p1 -= 2*dz;
                }
                if (p2 >= 0) {
                    current = new Location(current.getWorld(), current.getBlockX()+xs, current.getBlockY(), current.getBlockZ());
                    p2 -= 2*dz;
                }

                p1 += 2*dy;
                p2 += 2*dx;
                if (!VersionController.get().isPassable(current.getBlock())) return pre;
                pre = current;
            }
        }

        return destiny;
    }

    public static void updatePickedEntities() {
        Set<Map.Entry<Player, Entity>> entities;
        synchronized (pickedEntitiesAndPicker) {
            pickedEntitiesAndPicker.entrySet().removeIf(e -> !e.getValue().isValid()); // Entity no loger exists
            entities = pickedEntitiesAndPicker.entrySet();
        }

        for (Map.Entry<Player, Entity> e : entities) {
            Location expect = Ray.getPoint(e.getKey(), PICKED_ENTITY_DISTANCE),
                    newLocation = secureTeleport(e.getValue(), expect);

            if (newLocation == expect) {
                // grabbed objects face away from the player
                newLocation.setYaw(e.getKey().getLocation().getYaw());
            }
            Bukkit.getScheduler().callSyncMethod(PortalGun.plugin, () -> e.getValue().teleport(newLocation));
        }
    }
}

package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.cubes.CompanionCube;
import com.rogermiranda1000.portalgun.cubes.Cube;
import com.rogermiranda1000.portalgun.cubes.Cubes;
import com.rogermiranda1000.portalgun.utils.raycast.Ray;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.entities.EntityWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class onPortalgunEntity {
    public static final Set<String> entityPickBlacklist = new HashSet<>();
    private static final HashMap<Player, EntityWrapper> pickedEntitiesAndPicker = new HashMap<>();
    private static final float LAUNCH_VELOCITY_MULTIPLIER = 1.f,
                            PICKED_ENTITY_DISTANCE = 2.5f;

    public void onEntityPick(PlayerPickEvent event) {
        String entityPickedName = event.getEntityPicked().getType().name().toLowerCase();
        Cube cube;
        if ((cube = Cubes.getCube(event.getEntityPicked())) != null) {
            // companion cube picked
            if (!Cubes.isCubeSkeleton(event.getEntityPicked())) {
                // you have to pick the skeleton; simulate the event as you pick the other one
                PlayerPickEvent e2 = new PlayerPickEvent(event.getPlayer(), cube.getSkeleton());
                this.onEntityPick(e2);
                event.setCancelled(e2.isCancelled());
                return;
            }

            entityPickedName = "COMPANION_CUBE";
        }

        if (entityPickBlacklist.contains(entityPickedName)) {
            event.setCancelled(true);
            return;
        }

        EntityWrapper e = new EntityWrapper(event.getEntityPicked());
        e.disableGravity();
        synchronized (pickedEntitiesAndPicker) {
            pickedEntitiesAndPicker.put(event.getPlayer(), e);
        }
        // TODO sound
    }

    public void launchEntity(Player p) {
        EntityWrapper e;
        synchronized (pickedEntitiesAndPicker) {
            e = pickedEntitiesAndPicker.remove(p);
        }
        e.enableGravity();
        e.getEntity().setVelocity(e.getEntity().getLocation().toVector().subtract(p.getLocation().toVector()).multiply(LAUNCH_VELOCITY_MULTIPLIER));
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
            EntityWrapper e = pickedEntitiesAndPicker.get(p);
            return (e == null) ? null : e.getEntity();
        }
    }

    public static boolean isEntityPicked(Entity e) {
        synchronized (pickedEntitiesAndPicker) {
            return pickedEntitiesAndPicker.containsValue(e);
        }
    }

    public static void removeEntity(Player p) {
        EntityWrapper e;
        synchronized (pickedEntitiesAndPicker) {
            e = pickedEntitiesAndPicker.remove(p);
        }
        if (e == null) return;
        e.enableGravity();
    }

    public static void clear() {
        synchronized (pickedEntitiesAndPicker) {
            List<Player> toRemove = new ArrayList<>(pickedEntitiesAndPicker.keySet());
            for (Player p : toRemove) removeEntity(p);
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
        Set<Map.Entry<Player, EntityWrapper>> entities;
        synchronized (pickedEntitiesAndPicker) {
            pickedEntitiesAndPicker.entrySet().removeIf(e -> !e.getValue().getEntity().isValid()); // Entity no loger exists
            entities = pickedEntitiesAndPicker.entrySet();
        }

        for (Map.Entry<Player, EntityWrapper> e : entities) {
            Location expect = Ray.getPoint(e.getKey(), PICKED_ENTITY_DISTANCE),
                    newLocation = secureTeleport(e.getValue().getEntity(), expect);

            // grabbed objects face away from the player
            newLocation.setYaw(e.getKey().getLocation().getYaw());

            e.getValue().setLocation(newLocation);
        }
    }
}

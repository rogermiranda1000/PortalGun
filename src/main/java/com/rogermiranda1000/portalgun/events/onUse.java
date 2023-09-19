package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.Direction;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.blocks.ResetBlocks;
import com.rogermiranda1000.portalgun.blocks.ThermalReceivers;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.items.PortalGuns;
import com.rogermiranda1000.portalgun.portals.CeilingPortal;
import com.rogermiranda1000.portalgun.portals.FloorPortal;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.portals.WallPortal;
import com.rogermiranda1000.portalgun.utils.raycast.AABB;
import com.rogermiranda1000.portalgun.utils.raycast.Ray;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.particles.ParticleEntity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class onUse implements Listener {
    /**
     * How many blocks will the bean advance before playing a particle
     */
    private static final float BEAM_STEPS = 0.2f;
    
    public static int MAX_LENGTH;
    public static Sound CREATE_SOUND;

    private final onPortalgunEntity onEntityPick;
    public onUse(onPortalgunEntity onEntityPickEvent) {
        this.onEntityPick = onEntityPickEvent;
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (!PortalGuns.userUsedPortalGun(event)) return;

        event.setCancelled(true);

        boolean leftClick = (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR));
        this.playerUsedPortalGun(event.getPlayer(), leftClick);
    }

    public boolean playerUsedPortalGun(Player player, boolean leftClick) {
        if (onPortalgunEntity.haveEntityPicked(player)) {
            if (leftClick) this.onEntityPick.launchEntity(player);
            else this.onEntityPick.freeEntity(player);
            return true;
        }

        if (PortalGun.takeEntities && player.hasPermission("portalgun.entities") && !leftClick) {
            // maybe the player is facing an entity?
            Entity facing = getLookingEntity(player, PortalGun.MAX_ENTITY_PICK_RANGE, Portal.isEmptyBlock);
            if (facing != null && !ThermalReceivers.getInstance().isDecorate(facing)) {
                PlayerPickEvent ppe = new PlayerPickEvent(player, facing);
                this.onEntityPick.onEntityPick(ppe);
                if (!ppe.isCancelled()) return true;
                // else just ignore the pick and throw a portal
            }
        }

        /* opening a portal */
        if (!player.hasPermission("portalgun.open")) {
            player.sendMessage(PortalGun.plugin.getErrorPrefix() + Language.USER_NO_PERMISSIONS.getText());
            return false;
        }

        Block colliderBlock = getLookingBlock(player, onUse.MAX_LENGTH, Portal.isEmptyBlock);
        if (colliderBlock == null) {
            player.sendMessage(PortalGun.plugin.getErrorPrefix() + Language.PORTAL_FAR.getText());
            return false;
        }

        if (ResetBlocks.getInstance().insideResetBlock(colliderBlock.getLocation())) {
            player.playSound(player.getLocation(), onUse.CREATE_SOUND, 3.0F, 0.5F);
            // TODO fail animation
            return false;
        }

        if (!PortalGun.plugin.canSpawnPortal(colliderBlock.getLocation())) {
            player.sendMessage(PortalGun.plugin.getErrorPrefix() + Language.PORTAL_DENIED.getText());
            return false;
        }

        Portal p = getMatchingPortal(player, colliderBlock.getLocation(), leftClick,
                Direction.getDirection((Entity)player), player.getLocation().getBlock().getLocation().subtract(colliderBlock.getLocation()).toVector());

        if (p == null) {
            player.sendMessage(PortalGun.plugin.getErrorPrefix() + Language.PORTAL_DENIED.getText());
            return false;
        }

        // existing portal in that location? (and not replaced)
        if (p.collidesAndPersists()) {
            player.sendMessage(PortalGun.plugin.getErrorPrefix() + Language.PORTAL_COLLIDING.getText());
            return false;
        }

        Portal.setPortal(player, p);
        player.playSound(player.getLocation(), onUse.CREATE_SOUND, 3.0F, 0.5F);
        PortalGun.plugin.getLogger().info( Language.PORTAL_OPENED.getText (
                new String[] {"player", player.getName()},
                new String[] {"pos", colliderBlock.getWorld().getName() + " > " + colliderBlock.getX() + ", " + colliderBlock.getY() + ", " + colliderBlock.getZ()}
        ));
        if (PortalGun.castBeam) onUse.castBeam(player.getLocation().add(0, player.getEyeHeight(), 0), colliderBlock.getLocation(), p.getParticle());
        return true;
    }

    private static void castBeam(Location start, Location target, ParticleEntity particle) {
        Vector direction = start.getDirection().normalize().multiply(onUse.BEAM_STEPS);
        double startToTargetSquaredDistance = start.distanceSquared(target);

        Location bean = start.clone();
        do {
            bean.add(direction);
            particle.playParticle(bean.getWorld(), bean);
        } while (start.distanceSquared(bean) < startToTargetSquaredDistance);
    }

    @Nullable
    public static Entity getLookingEntity(Player p, int max, @Nullable Function<Block,Boolean> emptyBlock) {
        List<Entity> possible = p.getNearbyEntities(max, max, max);
        Ray ray = Ray.from(p);
        double d = -1;
        Entity closest = null;
        for (Entity e : possible) {
            if (e.equals(p)) continue;

            double dis = AABB.from(e).collidesD(ray, 0, max);
            if (dis != -1) {
                if (closest == null || dis < d) {
                    d = dis;
                    closest = e;
                }
            }
        }

        if (emptyBlock == null || closest == null) return closest;
        Block closestBlock = getLookingBlock(p, max, emptyBlock);
        if (closestBlock == null) return closest; // not looking any block
        return closest.getLocation().distanceSquared(p.getLocation()) < closestBlock.getLocation().distanceSquared(p.getLocation()) ? closest : null; // if the block is closer to the player then he's facing the block
    }

    @Nullable
    public static Entity getLookingEntity(Player p, int max) {
        return getLookingEntity(p, max, null);
    }

    @Nullable
    public static Block getLookingBlock(Player p, int max, Function<Block,Boolean> emptyBlock) {
        // raycasting
        BlockIterator iter = new BlockIterator(p, max);
        Block colliderBlock = iter.next();
        while (emptyBlock.apply(colliderBlock) && iter.hasNext()) colliderBlock = iter.next(); // TODO: blacklist blocks
        if (!iter.hasNext()) return emptyBlock.apply(colliderBlock) ? null /* we reached the max distance */ : colliderBlock;
        return colliderBlock;
    }

    private Portal getMatchingPortal(Player owner, Location loc, boolean isLeft, Direction direction, Vector v) {
        Direction []directions = getDirections(direction, v);

        for (Direction dir : directions) {
            Portal p = new WallPortal(owner, loc, dir.getOpposite(), isLeft);
            if (p.isValid()) return p;
        }

        if (v.getY() >= -1) {
            for (Direction dir : directions) {
                Portal p = new FloorPortal(owner, loc, dir.getOpposite(), isLeft);
                if (p.isValid()) return p;
            }
        }
        if (v.getY() <= -1) {
            for (Direction dir : directions) {
                Portal p = new CeilingPortal(owner, loc, dir.getOpposite(), isLeft);
                if (p.isValid()) return p;
            }
        }

        return null;
    }

    /**
     * @param dir Player's vision (by default)
     * @param v Vector from final block to player
     * @return Portal's possible direction(s)
     */
    private Direction []getDirections(Direction dir, Vector v) {
        double x = v.getX(), z = v.getZ();

        // TODO: arreglar este desaste
        if (x > 0) {
            if (z == 0) return new Direction[] {Direction.W};
            else if (z > 0) {
                if (x >= z) return new Direction[] {Direction.W, Direction.S};
                else return new Direction[] {Direction.S, Direction.W};
            }
            // z < 0
            else {
                if (x >= -z) return new Direction[] {Direction.W, Direction.N};
                else return new Direction[] {Direction.N, Direction.W};
            }
        }
        else if (x < 0) {
            if (z == 0) return new Direction[] {Direction.E};
            else if (z > 0) {
                if (-x >= z) return new Direction[] {Direction.E, Direction.S};
                else return new Direction[] {Direction.S, Direction.E};
            }
            // z < 0
            else {
                if (-x >= -z) return new Direction[] {Direction.E, Direction.N};
                else return new Direction[] {Direction.N, Direction.E};
            }
        }
        // x == 0
        else {
            if (z == 0) {
                // TODO: diagonals
                if (dir.diagonal()) return new Direction[] {Direction.N, Direction.W, Direction.E, Direction.S};
                // TODO: why? (Direction.getDirection((Entity)player)?)
                else if (dir == Direction.W) return new Direction[] {Direction.E};
                else if (dir == Direction.E) return new Direction[] {Direction.W};
                else return new Direction[] {dir};
            }
            if (z > 0) return new Direction[] {Direction.S};
            else if (z < 0) return new Direction[] {Direction.N};
        }

        return null;
    }
}

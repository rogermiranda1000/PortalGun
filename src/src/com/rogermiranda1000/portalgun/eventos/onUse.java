package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.Direction;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.versioncontroller.ItemManager;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.portals.CeilingPortal;
import com.rogermiranda1000.portalgun.portals.FloorPortal;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.portals.WallPortal;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class onUse implements Listener {
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!ItemManager.hasItemInHand(player, PortalGun.item, true)) return;
        if(event.getAction().equals(Action.PHYSICAL)) return;

        event.setCancelled(true);
        if (!player.hasPermission("portalgun.open")) {
            player.sendMessage(PortalGun.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
            return;
        }

        // raytracing
        BlockIterator iter = new BlockIterator(player, PortalGun.plugin.max_length);
        Block colliderBlock = iter.next();
        while (Portal.isEmptyBlock.apply(colliderBlock) && iter.hasNext()) colliderBlock = iter.next();

        Portal p = getMatchingPortal(player, colliderBlock.getLocation(), event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR),
                Direction.getDirection((Entity)player), player.getLocation().getBlock().getLocation().subtract(colliderBlock.getLocation()).toVector());

        if (p == null) {
            if (!iter.hasNext()) player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_FAR.getText());
            else player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_DENIED.getText());
            return;
        }

        // existing portal in that location? (and not replaced)
        if (p.collidesAndPersists()) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_COLLIDING.getText());
            return;
        }

        Portal.setPortal(player, p);
        PortalGun.plugin.getLogger().info( Language.PORTAL_OPENED.getText (
                new String[] {"player", player.getName()},
                new String[] {"pos", colliderBlock.getWorld().getName() + " > " + colliderBlock.getX() + ", " + colliderBlock.getY() + ", " + colliderBlock.getZ()}
        ));
    }

    Portal getMatchingPortal(Player owner, Location loc, boolean isLeft, Direction direction, Vector v) {
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
    Direction []getDirections(Direction dir, Vector v) {
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

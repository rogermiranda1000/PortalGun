package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.Direction;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.ItemManager;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.portals.CeilingPortal;
import com.rogermiranda1000.portalgun.portals.FloorPortal;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.portals.WallPortal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class onUse implements Listener {
    private boolean emptyBlock(Block b) {
        return (b.isPassable() || b.isLiquid());
    }

    // TODO: check only-certain-blocks
    private boolean validSupport(Block b) {
        /*if (PortalGun.config.getBoolean("only_certain_blocks") && !player.hasPermission("portalgun.overrideblocks") &&
                !PortalGun.instancia.b.contains(colliderBlockType.toString().toLowerCase() + ":" + String.valueOf(lastBlock.getDrops().iterator().next().getDurability()))) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_BLOCK_DENIED);
            return;
        }*/
        return b.getType().isSolid();
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!ItemManager.hasItemInHand(player, PortalGun.instancia.item)) return;
        if(event.getAction().equals(Action.PHYSICAL)) return;

        event.setCancelled(true);
        if (!player.hasPermission("portalgun.open")) {
            player.sendMessage(PortalGun.errorPrefix + Language.USER_NO_PERMISSIONS.getText());
            return;
        }

        // raytracing
        BlockIterator iter = new BlockIterator(player, PortalGun.instancia.max_length);
        Block colliderBlock = iter.next();
        while (emptyBlock(colliderBlock) && iter.hasNext()) colliderBlock = iter.next();

        Portal p = getMatchingPortal(colliderBlock.getLocation(), event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR),
                getDirections(Direction.getDirection((Entity)player), player.getLocation().getBlock().getLocation().subtract(colliderBlock.getLocation()).toVector()));

        if (p == null) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_DENIED.getText());
            return;
        }

        // existing portal in that location?
        if (p.collides()) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_COLLIDING.getText());
            return;
        }

        Portal.setPortal(player, p);
        PortalGun.instancia.getLogger().info( Language.PORTAL_OPENED.getText (
                new String[] {"player", player.getName()},
                new String[] {"pos", colliderBlock.getWorld().getName() + " > " + colliderBlock.getX() + ", " + colliderBlock.getY() + ", " + colliderBlock.getZ()}
        ));
    }

    Portal getMatchingPortal(Location loc, boolean isLeft, Direction []directions) {
        for (Direction dir : directions) {
            Portal p = new WallPortal(loc, dir.getOpposite(), isLeft);
            if (validPortal(p)) return p;
        }
        for (Direction dir : directions) {
            Portal p = new FloorPortal(loc, dir.getOpposite(), isLeft);
            if (validPortal(p)) return p;
        }
        for (Direction dir : directions) {
            Portal p = new CeilingPortal(loc, dir.getOpposite(), isLeft);
            if (validPortal(p)) return p;
        }

        return null;
    }

    boolean validPortal(Portal p) {
        for(Location l : p.getSupportLocations()) {
            if (!validSupport(l.getBlock())) return false;
        }
        for (Location l : p.getTeleportLocations()) {
            if (!emptyBlock(l.getBlock())) return false;
        }

        return true;
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
                if (!dir.diagonal()) return new Direction[] {dir};
            }
            if (z > 0) return new Direction[] {Direction.S};
            else if (z < 0) return new Direction[] {Direction.N};
        }

        return null;
    }
}

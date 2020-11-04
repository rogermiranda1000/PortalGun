package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.Direction;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.ItemManager;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.portals.WallPortal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.Set;

public class onUse implements Listener {
    private boolean emptyBlock(Block b) {
        return (b.isPassable() || b.isLiquid());
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
        Block colliderBlock = iter.next(), lastBlock;
        do {
            lastBlock = colliderBlock;
            colliderBlock = iter.next();
        } while (emptyBlock(colliderBlock) && iter.hasNext());

        // TODO: vector instead of looking direction
        Vector dif = colliderBlock.getLocation().subtract(lastBlock.getLocation()).toVector();
        Direction looking = Direction.getDirection(player);
        if (looking.diagonal()) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_DENIED.getText());
            return;
        }

        // TODO: check other blocks
        if (!colliderBlock.getType().isSolid()) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_DENIED.getText());
            return;
        }
        // TODO: check only-certain-blocks
        /*if (PortalGun.config.getBoolean("only_certain_blocks") && !player.hasPermission("portalgun.overrideblocks") &&
                !PortalGun.instancia.b.contains(colliderBlockType.toString().toLowerCase() + ":" + String.valueOf(lastBlock.getDrops().iterator().next().getDurability()))) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_BLOCK_DENIED);
            return;
        }*/

        Portal p = new WallPortal(colliderBlock.getLocation(), looking, event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR));

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
}

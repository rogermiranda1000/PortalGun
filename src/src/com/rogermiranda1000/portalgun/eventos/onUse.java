package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.Direction;
import com.rogermiranda1000.portalgun.LPortal;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.ItemManager;
import com.rogermiranda1000.portalgun.files.Language;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class onUse implements Listener {
    private boolean emptyBlock(Block b) {
        return (b.isPassable() || b.isLiquid());
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!ItemManager.hasItemInHand(player, PortalGun.instancia.item)) return;
        if(event.getAction() == Action.PHYSICAL) return;

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

        Location colliderBlockLocation = colliderBlock.getLocation(), lastBlockLocation = lastBlock.getLocation();
        Vector dif = colliderBlockLocation.subtract(lastBlockLocation).toVector();

        Material lastBlockType = lastBlock.getType();
        if (!lastBlockType.isSolid()) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_DENIED);
            return;
        }
        if (PortalGun.config.getBoolean("only_certain_blocks") && !player.hasPermission("portalgun.overrideblocks") &&
                !PortalGun.instancia.b.contains(lastBlockType.toString().toLowerCase() + ":" + String.valueOf(lastBlock.getDrops().iterator().next().getDurability()))) {
            player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_BLOCK_DENIED);
            return;
        }

    Direction looking = Direction.getDirection(player);
    if(last.getBlock().getType()==Material.AIR) {
        //Portal en el suelo
        double x = 0D;
        double z = 0D;
        last.setY(last.getY()-1D);
        last = PortalGun.instancia.getGroundBlock(looking.name(), last);
        if(last==null) {
            player.sendMessage(PortalGun.prefix+ PortalGun.config.getString("portal_failed"));
            return;
        }
        if(!last.getBlock().getType().isSolid()) {
            player.sendMessage(PortalGun.prefix+ PortalGun.config.getString("portal_denied"));
            return;
        }
        ground = true;
    }

    if(PortalGun.config.getBoolean("only_certain_blocks")) {
        if (lastBlock.getDrops().isEmpty() || last.getBlock().getDrops().isEmpty()) {
            player.sendMessage(PortalGun.prefix + PortalGun.config.getString("portal_failed"));
            return;
        }
        if (!player.hasPermission("portalgun.overrideblocks") &&
                (!PortalGun.instancia.b.contains(lastBlock.getType().toString().toLowerCase() + ":" + String.valueOf(lastBlock.getDrops().iterator().next().getDurability())) ||
                        !PortalGun.instancia.b.contains(last.getBlock().getType().toString().toLowerCase() + ":" + String.valueOf(lastBlock.getDrops().iterator().next().getDurability())))) {
            player.sendMessage(PortalGun.prefix + PortalGun.config.getString("denied_block"));
            return;
        }
    }

    if(!ground) {
        if (looking == Direction.N) block.setX(block.getX() + 1.0D);
        else if (looking == Direction.S) block.setX(block.getX() - 1.0D);
        else if (looking == Direction.E)  block.setZ(block.getZ() + 1.0D);
        else if (looking == Direction.W) block.setZ(block.getZ() - 1.0D);
        else {
            player.sendMessage(PortalGun.prefix+ PortalGun.config.getString("portal_failed"));
            return;
        }
    } else {
        block.setY(block.getY()+1);
        last.setY(last.getY()+1);
    }

    Location block2 = new Location(block.getWorld(), block.getX(), block.getY() + 1.0D, block.getZ());
    if (block.getBlock().getType() != Material.AIR || (block2.getBlock().getType() != Material.AIR && !ground) ||
            (ground && last.getBlock().getType()!=Material.AIR)) {
        player.sendMessage(PortalGun.prefix+ PortalGun.config.getString("portal_denied"));
        return;
    }

    Location loc1 = null;
    Location loc2 = null;
    if(!PortalGun.instancia.portales.containsKey(player.getName())) PortalGun.instancia.portales.put(player.getName(), new LPortal());

    LPortal p = PortalGun.instancia.portales.get(player.getName());
    if(!p.world[0].isEmpty()) loc1 = new Location(Bukkit.getServer().getWorld(p.world[0]), p.loc[0][0], p.loc[0][1], p.loc[0][2]);
    if(!p.world[1].isEmpty()) loc2 = new Location(Bukkit.getServer().getWorld(p.world[1]), p.loc[1][0], p.loc[1][1], p.loc[1][2]);
    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) { loc1 = block; }
    else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) { loc2 = block; }
    if (loc1 != null && loc2 != null && (loc1.equals(loc2) || (new Location(loc1.getWorld(), loc1.getX(), loc1.getY() + 1.0D, loc1.getZ())).equals(loc2) ||
            (new Location(loc1.getWorld(), loc1.getX(), loc1.getY() - 1.0D, loc1.getZ())).equals(loc2))) {
        player.sendMessage(PortalGun.prefix+ PortalGun.config.getString("same_portal"));
        return;
    }
    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
        p.loc[0][0] = block.getX();
        p.loc[0][1] = block.getY();
        p.loc[0][2] = block.getZ();
        p.dir[0] = looking.name().charAt(0);
        p.world[0] = block.getWorld().getName();
        p.down[0] = ground;
        //getLogger().info(String.valueOf(block.getWorld())+"-"+p.world[0]);
        loc1 = new Location(Bukkit.getServer().getWorld(p.world[0]), p.loc[0][0], p.loc[0][1], p.loc[0][2]);
    } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        p.loc[1][0] = block.getX();
        p.loc[1][1] = block.getY();
        p.loc[1][2] = block.getZ();
        p.dir[1] = looking.name().charAt(0);
        p.world[1] = block.getWorld().getName();
        p.down[1] = ground;
        loc2 = new Location(Bukkit.getServer().getWorld(p.world[1]), p.loc[1][0], p.loc[1][1], p.loc[1][2]);
        //getLogger().info(String.valueOf(Bukkit.getServer().getWorld(p.world[1]))+"-"+String.valueOf(loc2.getWorld()));
    }
    if (loc1 != null && loc2 != null) {
        player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 3.0F, 0.5F);
        PortalGun.instancia.getLogger().info(String.valueOf(player.getName()) + " >> " + block.getBlockX() + ", " + block.getBlockY() + ", " + block.getBlockZ() + " (" + looking + ")");
        String message = PortalGun.config.getString("half_portal_opened").replace("[pos]", String.valueOf(block.getX()) + ", " + block.getY() + ", " + block.getZ());
        player.sendMessage(PortalGun.clearPrefix + ChatColor.GREEN + message);
        PortalGun.instancia.getLogger().info(String.valueOf(player.getName()) + " has opened a portal.");
        player.sendMessage(PortalGun.clearPrefix + ChatColor.GREEN + PortalGun.config.getString("portal_opened"));
        PortalGun.instancia.cancelPortals(true);
    } else {
        player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 3.0F, 0.5F);
        PortalGun.instancia.getLogger().info(String.valueOf(player.getName()) + " >> " + block.getBlockX() + ", " + block.getBlockY() + ", " + block.getBlockZ() + " (" + looking + ")");
        String message = PortalGun.config.getString("half_portal_opened").replace("[pos]", block.getX() + ", " + block.getY() + ", " + block.getZ());
        player.sendMessage(PortalGun.clearPrefix + ChatColor.GREEN + message);
    }
    }
}

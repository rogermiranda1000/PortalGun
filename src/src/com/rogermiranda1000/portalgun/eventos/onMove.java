package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.Direction;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.portals.Portal;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class onMove implements Listener {
    // TODO: all entity instead of player
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location loc = e.getTo().getBlock().getLocation();
        if(player.getInventory().getBoots()!=null && player.getInventory().getBoots().equals(PortalGun.instancia.botas)) player.setFallDistance(0);

        Portal portal = Portal.getPortal(loc);
        if (portal == null) return;

        // TODO: velocity direction instead of looking direction
        if (Direction.getDirection(player.getLocation().getYaw()) == portal.getDirection()) portal.teleportToDestiny(player);
    }
}

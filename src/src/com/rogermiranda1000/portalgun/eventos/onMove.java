package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.portals.Portal;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class onMove implements Listener {
    static

    // TODO: all entity instead of player
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (e.getTo() == null) return;
        Location loc = e.getTo().getBlock().getLocation();

        if(player.getInventory().getBoots()!=null && player.getInventory().getBoots().equals(PortalGun.instancia.botas)) player.setFallDistance(0);

        Portal portal = Portal.getPortal(loc);
        if (portal == null) return;

        // TODO: velocity direction instead
        //if(loc.clone().add(0.5f, 0.5f, 0.5f).distance(e.getTo()) <= 0.95f)
        /*if (Direction.getDirection(player.getLocation().getYaw()).equals(portal.getDirection().getOpposite()))*/ //{
            if(portal.teleportToDestiny(player, portal.getLocationIndex(loc))) player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 3.0F, 0.5F);
        //}
    }
}

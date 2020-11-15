package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.files.Config;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.portals.WallPortal;
import com.rogermiranda1000.portalgun.versioncontroller.SoundManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class onMove implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        Vector delta = (e.getTo().clone()).subtract(e.getFrom()).multiply(0.2f /* blocks/second */ * 0.05f /* second/tick */).toVector();
        if (delta.length() == 0.f) return; // not moving?

        Player player = e.getPlayer();
        Location loc = e.getTo().getBlock().getLocation();

        if(player.getInventory().getBoots()!=null && player.getInventory().getBoots().equals(PortalGun.botas)) player.setFallDistance(0);

        Portal portal = Portal.getPortal(loc);
        if (portal == null) return;

        if (Config.ONLY_YOUR_PORTALS.getBoolean() && !player.equals(portal.getOwner())) return;

        // TODO: player velocity??
        if (portal instanceof WallPortal) {
            double approachVelocitySquare = delta.dot(portal.getApproachVector());
            if(approachVelocitySquare <= 0.f) return; // not approaching
            approachVelocitySquare = Math.pow(approachVelocitySquare, 2);

            // approachVelocitySquare < deltaX.dot(deltaX) - approachVelocitySquare
            // traveling faster in other direction?
            if (2*approachVelocitySquare < delta.dot(delta) /* square of each element */) return;
        }
        else {
            if (!(player.getVelocity().dot(portal.getApproachVector()) >= 0.f)) return;
        }
        //player.setVelocity(playerVelocity);

        if(portal.teleportToDestiny(player, portal.getLocationIndex(loc))) player.playSound(player.getLocation(), SoundManager.getTeleportSound(), 3.0F, 0.5F);
    }
}

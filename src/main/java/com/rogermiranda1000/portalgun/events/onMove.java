package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.blocks.ResetBlocks;
import com.rogermiranda1000.portalgun.files.Config;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.portals.WallPortal;
import com.rogermiranda1000.versioncontroller.VersionController;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class onMove implements Listener {
    private final onEmancipator emancipatorGridEvent;

    public onMove(onEmancipator emancipatorGridEvent) {
        this.emancipatorGridEvent = emancipatorGridEvent;
    }

    public onEmancipator getEmancipatorGridEvent() {
        return this.emancipatorGridEvent;
    }

    @EventHandler
    public void onMoveMListener(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        Vector delta = (e.getTo().clone()).subtract(e.getFrom()).multiply(0.2f /* blocks/second */ * 0.05f /* second/tick */).toVector();
        if (delta.length() == 0.f) return; // not moving?

        Player player = e.getPlayer();
        Location loc = e.getTo().getBlock().getLocation();

        if(player.getInventory().getBoots()!=null && player.getInventory().getBoots().equals(PortalGun.botas)) player.setFallDistance(0);
        if (ResetBlocks.getInstance().insideResetBlock(loc)) this.emancipatorGridEvent.onEntityGoesThroughEmancipationGrid(player);

        Portal portal = Portal.getPortal(loc);
        if (portal == null) return;

        if (Config.ONLY_YOUR_PORTALS.getBoolean() && !player.equals(portal.getOwner())) return;

        Location destiny = portal.getDestiny(portal.getLocationIndex(loc));
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
            synchronized (PortalGun.teleportedEntities) {
                if (PortalGun.teleportedEntities.containsKey(player)) return; // prevent bouncing from one portal to another
            }
        }

        if(portal.teleportToDestiny(player, VersionController.get().getVelocity(e), destiny)) {
            synchronized (PortalGun.teleportedEntities) {
                PortalGun.teleportedEntities.put(player, destiny);
            }
            player.playSound(player.getLocation(), Config.TELEPORT_SOUND.getSound(), 3.0F, 0.5F);
        }
    }
}

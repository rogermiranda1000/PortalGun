package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.files.Config;
import com.rogermiranda1000.portalgun.portals.Portal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class onLeave implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        onPortalgunEntity.removeEntity(event.getPlayer());
        if (!Config.REMOVE_ON_LEAVE.getBoolean()) return;

        Portal.removePortal(event.getPlayer().getUniqueId());
    }
}

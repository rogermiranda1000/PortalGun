package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.files.Config;
import com.rogermiranda1000.portalgun.portals.Portal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class onLeave implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (!Config.REMOVE_ON_LEAVE.getBoolean()) return;

        Portal.removePortal(event.getPlayer().getUniqueId());
    }
}

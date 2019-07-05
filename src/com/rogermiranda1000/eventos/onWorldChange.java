package com.rogermiranda1000.eventos;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class onWorldChange implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (!PortalGun.config.getBoolean("remove_portals_on_world_change") || !PortalGun.instancia.portales.containsKey(player.getName())) return;
        PortalGun.instancia.cancelPortals(false);
        PortalGun.instancia.portales.remove(player.getName());
        PortalGun.instancia.cancelPortals(true);
        player.sendMessage(PortalGun.prefix+ PortalGun.config.getString("portal_removed_by_world_change"));
    }
}

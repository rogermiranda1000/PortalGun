package com.rogermiranda1000.eventos;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class onLeave implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (!PortalGun.instancia.ROL.booleanValue())
            return;
        String nick = event.getPlayer().getName();
        if (!PortalGun.instancia.portales.containsKey(nick)) return;
        PortalGun.instancia.cancelPortals(false);
        PortalGun.instancia.portales.remove(nick);
        PortalGun.instancia.cancelPortals(true);
    }
}

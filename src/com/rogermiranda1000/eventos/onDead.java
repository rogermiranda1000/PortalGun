package com.rogermiranda1000.eventos;

import com.rogermiranda1000.portalgun.PortalGun;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class onDead implements Listener {
    @EventHandler
    public void onDead(PlayerDeathEvent e) {
        String nick = e.getEntity().getName();
        if(!PortalGun.config.getBoolean("delete_portals_on_death")) return;
        if(!PortalGun.instancia.portales.containsKey(nick)) return;

        PortalGun.instancia.cancelPortals(false);
        PortalGun.instancia.portales.remove(nick);
        PortalGun.instancia.cancelPortals(true);
        e.getEntity().getPlayer().sendMessage("§6§l[PortalGun] " + ChatColor.RED + PortalGun.config.getString("portal_removed_by_death"));
    }
}

package com.rogermiranda1000.portalgun.eventos;

import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.portals.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class onDead implements Listener {
    @EventHandler
    public void onDead(PlayerDeathEvent e) {
        Player p = (Player)e.getEntity();
        if(!PortalGun.config.getBoolean("delete_portals_on_death")) return;

        if(Portal.removePortal(p)) p.sendMessage(PortalGun.errorPrefix + Language.USER_DEATH.getText());
    }
}

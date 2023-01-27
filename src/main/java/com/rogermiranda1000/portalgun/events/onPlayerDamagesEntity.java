package com.rogermiranda1000.portalgun.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class onPlayerDamagesEntity implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamages(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        if (event.getEntity().equals(onPortalgunEntity.getEntityPicked((Player) event.getDamager()))) event.setCancelled(true);
    }
}

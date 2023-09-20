package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.events.onUse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ThermalBeamsGuard implements Listener {
    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent e) {
        if (!ThermalBeams.getInstance().isDecorate(e.getRightClicked())) return;

        e.setCancelled(true);
        // simulate a right click
        PortalGun.plugin.getListener(onUse.class).onPlayerUse(new PlayerInteractEvent(
                e.getPlayer(),
                Action.RIGHT_CLICK_AIR,
                null,null,null // not used
        ));
    }

    // <1.9 doesn't have ArmorStand#setInvulnerable
    @EventHandler
    public void onEntityDamages(EntityDamageEvent e) {
        if (!ThermalBeams.getInstance().isDecorate(e.getEntity())) return;

        e.setCancelled(true);
    }
}

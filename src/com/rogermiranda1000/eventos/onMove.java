package com.rogermiranda1000.eventos;

import com.rogermiranda1000.portalgun.Portal;
import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class onMove implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location loc = e.getTo().getBlock().getLocation();
        if(player.getInventory().getBoots()!=null && player.getInventory().getBoots().equals(PortalGun.instancia.botas)) player.setFallDistance(0);
        for (String s : PortalGun.instancia.portales.keySet()) {
            Portal p = PortalGun.instancia.portales.get(s);
            if(p.world[0]=="" || p.world[1]=="") continue;
            double l[] = {loc.getX(), loc.getY(), loc.getZ()};
            int result = p.contains(l,loc.getWorld().getName());
            double temp[] = {e.getFrom().getBlock().getLocation().getX(),e.getFrom().getBlock().getLocation().getY(),e.getFrom().getBlock().getLocation().getZ()};
            if(result==-1) continue;
            if(p.down[result] && result==p.contains(temp,loc.getWorld().getName())) continue;
            if(PortalGun.config.getBoolean("use_only_your_portals") && !player.hasPermission("portalgun.overrideotherportals") && player.getName()!=s) continue;
            if (PortalGun.instancia.entidad_portal.contains(player)) PortalGun.instancia.entidad_portal.remove(player);
            String Nlooking = PortalGun.getCardinalDirection(player);
            if(!Nlooking.equalsIgnoreCase(String.valueOf(Nlooking.charAt(0)))) continue;

            int op = 0;
            if(result==0) op = 1;
            if(p.dir[result]==Nlooking.charAt(0) || p.down[result])
                PortalGun.instancia.teletransporte(new Location(Bukkit.getServer().getWorld(p.world[op]), p.loc[op][0], p.loc[op][1], p.loc[op][2]), player,
                        String.valueOf(p.dir[op]), String.valueOf(p.dir[result]), p.down[op], p.down[result]);
        }
    }
}

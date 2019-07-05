package com.rogermiranda1000.eventos;

import org.bukkit.event.Listener;

public class onPlaceBlock implements Listener {
    /*@EventHandler
          public void onPlaceBlock(BlockPlaceEvent e) {
            Location block = e.getBlock().getLocation();
            Location down = new Location(block.getWorld(), block.getX(), block.getY() - 1.0D, block.getZ());
            if (!PortalGun.this.portal1.containsValue(block) && !PortalGun.this.portal2.containsValue(block) && !PortalGun.this.portal1.containsValue(down) &&
            		!PortalGun.this.portal2.containsValue(down))
              return;  for (int x = 0; x != 2; x++) {
              Location loc = block;
              if (x == 1) loc = down;

              for (int y = 0; y != 2; y++) {
                HashMap<String, Location> portal = PortalGun.this.portal1;
                if (y == 1) portal = PortalGun.this.portal2;

                if (portal.containsValue(loc)) {
                  for (String o : portal.keySet()) {
                    if (((Location)portal.get(o)).equals(loc)) {
                      Player rompedor = e.getPlayer();
                      if (!rompedor.hasPermission("portalgun.destroy")) {
                        e.setCancelled(true);

                        return;
                      }
                      Player destino = Bukkit.getPlayer(o);
                      String message = PortalGun.config.getString("your_portal_destroyed").replace("[player]", e.getPlayer().getName());
                      if (destino.getName().equalsIgnoreCase(o)) destino.sendMessage(prefix+ message);

                      String message2 = PortalGun.config.getString("destroy_portal").replace("[player]", o);
                      rompedor.sendMessage(prefix+ message2);

                      PortalGun.this.cancelPortals(false);
                      PortalGun.this.portal1.remove(o);
                      PortalGun.this.portal2.remove(o);
                      PortalGun.this.cancelPortals(true);
                      return;
                    }
                  }
                }
              }
            }
          }*/
}

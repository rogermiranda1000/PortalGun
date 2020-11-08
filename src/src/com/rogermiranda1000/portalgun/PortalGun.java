package com.rogermiranda1000.portalgun;

import java.util.*;

import com.rogermiranda1000.portalgun.eventos.*;
import com.rogermiranda1000.portalgun.files.FileManager;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.versioncontroller.VersionController;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class PortalGun extends JavaPlugin
{
    public static PortalGun plugin;

    public static ItemStack item;
    public static ItemStack botas;
    public static final String clearPrefix=ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "[PortalGun] " + ChatColor.GREEN.toString(), errorPrefix=clearPrefix+ChatColor.RED;
    private static final int particleDelay = 2;
    private static final HashMap<Entity, Location> teleportedEntities = new HashMap<>();

    @Override
    public void onEnable() {
        PortalGun.plugin = this;

        FileManager.loadFiles();

        botas = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) botas.getItemMeta();
        meta2.setDisplayName(ChatColor.GREEN.toString() + "Portal Boots");
        // TODO: unbreakable alternative
        if (VersionController.getVersion() > 10) meta2.setUnbreakable(true);
        meta2.setColor(Color.WHITE);
        botas.setItemMeta(meta2);
        botas.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    
        // Particles
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, PortalGun::playAllParticles, 0, PortalGun.particleDelay);
        // TODO: configuration "only players teleports"
        // Entities
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, ()->{
            PortalGun.updateTeleportedEntities();
            PortalGun.teleportEntities();
        }, 1, PortalGun.particleDelay*3);

        // Events
        getServer().getPluginManager().registerEvents(new onDead(), this);
        getServer().getPluginManager().registerEvents(new onLeave(), this);
        getServer().getPluginManager().registerEvents(new onMove(), this);
        getServer().getPluginManager().registerEvents(new onTab(), this);
        getServer().getPluginManager().registerEvents(new onUse(), this);

        // Commands
        this.getCommand("portalgun").setExecutor(new onCommand());
    }

    public static void printErrorMessage(String txt) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED.toString() + "[PortalGun] " + txt);
    }

    private static void playAllParticles() {
        for (Portal p: Portal.getPortals()) {
            if (p.getPosition().getChunk().isLoaded()) p.playParticle();
        }
    }

    // TODO: don't teleport Item Frames
    private static void teleportEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity e : world.getEntities().toArray(new Entity[0])) {
                if (e instanceof Player) continue;
                if (PortalGun.teleportedEntities.containsKey(e)) continue;

                final Location entityBlockLocation = e.getLocation().getBlock().getLocation();
                final Portal portal = Portal.getPortal(entityBlockLocation);
                if (portal == null) continue;

                // TODO: horses
                final Location destinyLocation = portal.getDestiny(portal.getLocationIndex(entityBlockLocation));
                if (destinyLocation == null) continue;

                if (destinyLocation.getWorld().equals(entityBlockLocation.getWorld())) {
                    if(portal.teleportToDestiny(e, destinyLocation)) PortalGun.teleportedEntities.put(e, destinyLocation);
                }
                else {
                    // Async does not support teleport between worlds
                    Bukkit.getScheduler().callSyncMethod(PortalGun.plugin, () -> {
                        if (portal.teleportToDestiny(e, destinyLocation)) PortalGun.teleportedEntities.put(e, destinyLocation);
                        return null;
                    });
                }
            }
        }
    }

    /**
     * sees if teleported entities has moved (therefore, must be removed from the list)
     */
    private static void updateTeleportedEntities() {
        PortalGun.teleportedEntities.entrySet().removeIf(e -> !e.getKey().isValid()); // Entity no loger exists
        PortalGun.teleportedEntities.entrySet().removeIf(e -> !e.getKey().getLocation().getBlock().getLocation().equals(e.getValue())); // Entity has moved
    }

    @Override
    public void onDisable() {
      // TODO: Guardar portales
	  /*if (config.getBoolean("keep_portals_on_stop")) {
		  getLogger().info("Saving portals...");
          try {
		        File file = new File(getDataFolder(), "portal.yml");
		        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                for (String n : portales.keySet()) {
                    bw.write(n + ">" + portales.get(n).Save());
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            } catch (IOException e) { e.printStackTrace(); }
	  }*/
    }
}

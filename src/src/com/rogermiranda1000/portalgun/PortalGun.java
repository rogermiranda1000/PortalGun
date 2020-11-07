package com.rogermiranda1000.portalgun;

import java.io.File;
import java.util.*;

import com.rogermiranda1000.portalgun.eventos.*;
import com.rogermiranda1000.portalgun.files.FileManager;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.versioncontroller.BlockManager;
import com.rogermiranda1000.portalgun.versioncontroller.VersionController;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class PortalGun extends JavaPlugin
{
    public static PortalGun plugin;
    public static FileConfiguration config;

    public static ItemStack item;
    public static ItemStack botas;
    public static final String clearPrefix=ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "[PortalGun] " + ChatColor.GREEN.toString(), errorPrefix=clearPrefix+ChatColor.RED;
    private static final int particleDelay = 2;
    private static final HashMap<Entity, Location> teleportedEntities = new HashMap<>();

    boolean tp_log;
    public boolean ROL;
    public boolean public_portals;
    public int max_length;
    public List<String> b;

    @Override
    public void onEnable() {
        PortalGun.plugin = this;

        // TODO: config
      HashMap<String,String> c = new HashMap<>();
      c.put("max_portal_length", "80");
      c.put("all_portal_particles", "false");
      c.put("teleport_log", "true");
      c.put("portalgun_material", "BLAZE_ROD");
      c.put("portal1_particle", "FLAME");
      c.put("portal2_particle", "VILLAGER_HAPPY");
      c.put("remove_on_leave", "true");
      c.put("keep_portals_on_stop", "false");
      c.put("remove_on_death", "false");
      c.put("only_certain_blocks", "false");
      c.put("use_only_your_portals", "false");
        c.put("remove_portals_on_world_change", "false");
        c.put("language", "english");
        // TODO: array list
    config = getConfig();

    ArrayList<Object> allowedBlocks = new ArrayList<>();
    //Create/actualize config file
    try {
      if (!getDataFolder().exists()) getDataFolder().mkdir();
      File file = new File(getDataFolder(), "config.yml");
      boolean need = false;

      if (!file.exists()) {
          getLogger().info("Creating config.yml...");
          file.createNewFile();
          need = true;
      }

        for(Map.Entry<String, String> entry : c.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(!getConfig().isSet(key)) {
                if(value=="true") getConfig().set(key,Boolean.valueOf(true));
                else if(value=="false") getConfig().set(key,Boolean.valueOf(false));
                else if(value=="80") getConfig().set(key,Integer.valueOf(80));
                else getConfig().set(key,value);
                need = true;
            }
        }

        if(need) saveConfig();

        for (String txt : config.getStringList("valid_blocks")) {
            Object o = BlockManager.getMaterial(txt);
            if (o != null) allowedBlocks.add(o);
        }

    } catch (Exception e) {
      e.printStackTrace();
    }



        // TODO: lava restriction?
        // TODO: isPassable?
        Portal.isEmptyBlock = b->!b.getType().isSolid();

        Portal.isValidBlock = b->b.getType().isSolid() && (!config.getBoolean("only_certain_blocks") || allowedBlocks.contains(BlockManager.getObject(b)));

    // TODO: Cargar portales
    /*if (config.getBoolean("keep_portals_on_stop")) {
      getLogger().info("Loading portals...");
      File file = new File(getDataFolder(), "portal.yml");
      if(file.exists()) {
          try {
              BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
              String l;
              while ((l=br.readLine())!=null) {
                  String[] args = l.split(">");
                  if(args.length!=3) continue;
                  portales.put(args[0], new LPortal(args[1].split(","),args[2].split(",")));
              }
              br.close();
          } catch (Exception e) { e.printStackTrace(); }

        cancelPortals(true);
      }
    }*/

        String portalgunMaterialString = config.getString("portalgun_material");
        if (portalgunMaterialString == null) PortalGun.printErrorMessage("portalgun_material is not setted in config file!");
        else {
            Material portalgunMaterial = Material.getMaterial(portalgunMaterialString);
            if (portalgunMaterial == null) PortalGun.printErrorMessage("PortalGun's item (" + portalgunMaterialString + ") does not exists.");
            else {
                PortalGun.item = new ItemStack(portalgunMaterial);
                ItemMeta meta = PortalGun.item.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "PortalGun");
                PortalGun.item.setItemMeta(meta);
                PortalGun.item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
            }
        }

      botas = new ItemStack(Material.LEATHER_BOOTS);
      LeatherArmorMeta meta2 = (LeatherArmorMeta) botas.getItemMeta();
      meta2.setDisplayName(ChatColor.GREEN.toString() + "Portal Boots");
      // TODO: unbreakable alternative
      if (VersionController.getVersion() > 10) meta2.setUnbreakable(true);
      meta2.setColor(Color.WHITE);
      botas.setItemMeta(meta2);
      botas.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    
    max_length = config.getInt("max_portal_length");
    this.tp_log = config.getBoolean("teleport_log");
        //Portal.allParticlesAtOnce = config.getBoolean("all_portal_particles");
        // TODO: 1.8 Effect (instead of Particle)
        try {
            Portal.setParticle(Particle.valueOf(config.getString("portal1_particle")), true);
        } catch (IllegalArgumentException IAEx) {
            PortalGun.printErrorMessage("Particle '" + config.getString("portal1_particle") + "' does not exists.");
        }
        try {
            Portal.setParticle(Particle.valueOf(config.getString("portal2_particle")), false);
        } catch (IllegalArgumentException IAEx) {
            PortalGun.printErrorMessage("Particle '" + config.getString("portal2_particle") + "' does not exists.");
        }
    ROL = config.getBoolean("remove_on_leave");
      public_portals = !config.getBoolean("use_only_your_portals");


        FileManager.loadFiles();

    // Particles
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, PortalGun::playAllParticles, 0, PortalGun.particleDelay);
        // Entities
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, ()->{
            PortalGun.updateTeleportedEntities();
            PortalGun.teleportEntities();
        }, 0, PortalGun.particleDelay*3);

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

  private static void teleportEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity e : world.getEntities()) {
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

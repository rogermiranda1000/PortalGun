package com.rogermiranda1000.portalgun;

import java.io.File;
import java.util.*;

import com.rogermiranda1000.portalgun.eventos.*;
import com.rogermiranda1000.portalgun.files.FileManager;
import com.rogermiranda1000.portalgun.portals.Portal;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
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
    public static final String clearPrefix=ChatColor.GOLD+""+ChatColor.BOLD+"[PortalGun] ", errorPrefix=clearPrefix+ChatColor.RED;
    private static int particleDelay = 2;

    boolean tp_log;
    public boolean ROL;
    public boolean public_portals;
    public int max_length;
    public List<String> b;

    @Override
    public void onEnable() {
        getLogger().info("Plugin activated.");

        PortalGun.plugin = this;

        FileManager.loadFiles();

        Portal.isEmptyBlock = b->{
            return (b.isPassable() || b.isLiquid());
        };

        // TODO: check only-certain-blocks
        Portal.isValidBlock = b->{
            /*if (PortalGun.config.getBoolean("only_certain_blocks") && !player.hasPermission("portalgun.overrideblocks") &&
                    !PortalGun.instancia.b.contains(colliderBlockType.toString().toLowerCase() + ":" + String.valueOf(lastBlock.getDrops().iterator().next().getDurability()))) {
                player.sendMessage(PortalGun.errorPrefix + Language.PORTAL_BLOCK_DENIED);
                return;
            }*/
            return b.getType().isSolid();
        };

        // TODO: config
      HashMap<String,String> c = new HashMap<String, String>();
      c.put("max_portal_length", "80");
      c.put("all_portal_particles", "false");
      c.put("teleport_log", "true");
      c.put("portalgun_material", "BLAZE_ROD");
      c.put("portal1_particle", "FLAME");
      c.put("portal2_particle", "VILLAGER_HAPPY");
      c.put("remove_on_leave", "true");
      c.put("keep_portals_on_stop", "false");
      c.put("delete_portals_on_death", "false");
      c.put("only_certain_blocks", "false");
      c.put("use_only_your_portals", "false");
        c.put("remove_portals_on_world_change", "false");
        c.put("language", "english");
      c.put("blocks", "wool:0,quartz_block:0,quartz_block:1,quartz_block:2,concrete:0");
    config = getConfig();

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

    } catch (Exception e) {
      e.printStackTrace();
    }

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

      this.item = new ItemStack(Material.getMaterial(config.getString("portalgun_material")));
      ItemMeta meta = this.item.getItemMeta();
      meta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"PortalGun");
      this.item.setItemMeta(meta);
      this.item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

      botas = new ItemStack(Material.LEATHER_BOOTS);
      LeatherArmorMeta meta2 = (LeatherArmorMeta) botas.getItemMeta();
      meta2.setDisplayName(ChatColor.GREEN+"Portal Boots");
      meta2.setUnbreakable(true);
      meta2.setColor(Color.WHITE);
      botas.setItemMeta(meta2);
      botas.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    
    max_length = config.getInt("max_portal_length");
    this.tp_log = config.getBoolean("teleport_log");
        //Portal.allParticlesAtOnce = config.getBoolean("all_portal_particles");
        Portal.setParticle(Particle.valueOf(config.getString("portal1_particle")), true);
        Portal.setParticle(Particle.valueOf(config.getString("portal2_particle")), false);
    ROL = config.getBoolean("remove_on_leave");
      public_portals = !config.getBoolean("use_only_your_portals");
    b = Arrays.asList(PortalGun.config.getString("blocks").replace(" ", "").toLowerCase().split(","));
    for(int x = 0; x<b.size(); x++) {
        if(!b.get(x).contains(":")) b.set(x, b.get(x)+":0");
    }

    // TODO: Async
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for (Portal p: Portal.getPortals()) {
                    if (p.getPosition().getChunk().isLoaded()) p.playParticle();
                }
            }}, 0, PortalGun.particleDelay);


    // Events
      getServer().getPluginManager().registerEvents(new onDead(), this);
      getServer().getPluginManager().registerEvents(new onLeave(), this);
      getServer().getPluginManager().registerEvents(new onMove(), this);
      getServer().getPluginManager().registerEvents(new onTab(), this);
        getServer().getPluginManager().registerEvents(new onUse(), this);

        // Commands
        this.getCommand("portalgun").setExecutor(new onCommand());
  }

    @Override
  public void onDisable() {
	  getLogger().info("Plugin disabled.");
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
  
  /*public void teletransporte(Location loc, Entity player, String look, String lastL, boolean down, boolean lastD) {
	  if (!this.entidad_portal.contains(player)) { this.entidad_portal.add(player); }
	  else { return; }
	  
	  if (loc.getWorld().getPlayers().size() == 0 && !(player instanceof Player)) return;
	  if (!player.getPassengers().isEmpty() && player.getPassengers().get(0) instanceof Player) player = (Entity)player.getPassengers().get(0);
	  if (this.tp_log) getLogger().info("Teleporting " + player.getName() + "...");

      Vector vel = player.getVelocity().clone();
      double predominante = 0D;
      if(lastD) predominante = vel.getY();
      else {
          if(lastL.equalsIgnoreCase("N") || lastL.equalsIgnoreCase("S")) predominante = vel.getX();
          else predominante = vel.getZ();
      }
      if(predominante<0) predominante *= -1;

      if(!down) {
          int yaw = 0;
          vel = new Vector(0D, 0D, predominante);
          if (look.equalsIgnoreCase("N")) {
              yaw = -90;
              vel = new Vector(predominante, 0D, 0D);
          }
          else if (look.equalsIgnoreCase("S")) {
              yaw = 90;
              vel = new Vector(-predominante, 0D, 0D);
          }
          else if (look.equalsIgnoreCase("W")) {
              yaw = 180;
              vel = new Vector(0D, 0D, -predominante);
          }
          loc.setYaw(yaw);
      }
      else {
          loc.setYaw(player.getLocation().getYaw());
          vel = new Vector(0D, predominante, 0D);
      }
	  loc.setPitch(player.getLocation().getPitch());
	  Entity ride = null;
	  for (Entity ent: loc.getChunk().getEntities()) {
		  if (!ent.getPassengers().isEmpty() && ent.getPassengers() == player) { ride = ent; break; }
	  }
	  if (ride != null) {
		  ride.removePassenger(player);
		  ride.teleport(new Location(loc.getWorld(), loc.getX() + 0.5D, loc.getY(), loc.getZ() + 0.5D, loc.getYaw(), loc.getPitch()));
		  if (!this.entidad_portal.contains(ride)) this.entidad_portal.add(ride);
	  } else {
		  player.teleport(new Location(loc.getWorld(), loc.getX() + 0.5D, loc.getY(), loc.getZ() + 0.5D, loc.getYaw(), loc.getPitch()));
	  }
	  if (player instanceof Player) {
		  ((Player)player).getPlayer().playSound(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 3.0F, 0.5F);
	  }

	  player.setVelocity(vel);
  }*/

}

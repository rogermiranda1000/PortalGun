package com.rogermiranda1000.portalgun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class PortalGun extends JavaPlugin
{
    public static PortalGun instancia;
  public static FileConfiguration config;
   public HashMap<String, Portal> portales = new HashMap<String, Portal>();
  public List<Entity> entidad_portal = new ArrayList<Entity>();
  
  int tasks;
  public ItemStack item;
  public ItemStack botas;
  Boolean all_particles;
  Boolean tp_log;
  int part_task = 0;
  int delay = 2;
  public static final String clearPrefix=ChatColor.GOLD+""+ChatColor.BOLD+"[PortalGun] ",prefix=clearPrefix+ChatColor.RED;

  Particle Pportal1;
  Particle Pportal2;
  public Boolean ROL;
  public int max_length;
  
  public void onEnable() {
    getLogger().info("Plugin activated.");

      instancia = this;


      HashMap<String,String> c = new HashMap<String, String>();
      c.put("portal_denied", "You can't open a portal here.");
      c.put("no_permissions", "You don't have permissions to do this.");
      c.put("half_portal_opened", "Portal opened at [pos].");
      c.put("portal_opened", "The portals have been linked.");
      c.put("portal_failed", "Error at opening the portal!");
      c.put("no_portals", "You don't have any opened portals right now.");
      c.put("same_portal", "You can't place both portals at the same block!");
      c.put("portal_remove", "You deleted successfully your portals.");
      c.put("portal_removed_by_death", "Your portals have been deleted due to your death.");
      c.put("portal_removed_by_world_change", "You can't keep portals between worlds.");
      c.put("force_portal_remove", "Deleted all portals.");
      c.put("give_gun", "PortalGun gived!");
      c.put("info_get_PortalGun", "Get your PortalGun.");
      c.put("info_remove_portals", "Delete your active portals.");
      c.put("info_remove_all_portals", "Delete all the active portals.");
      c.put("destroy_portal", "You have destroyed [player]'s portal.");
      c.put("your_portal_destroyed", "Your portal has been destroyed by [player].");
      c.put("denied_block", "You can't open a portal in that block.");
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
      c.put("blocks", "QUARTZ_BLOCK");
    config = getConfig();

    //Create/actualize config file
    try {
      if (!getDataFolder().exists()) getDataFolder().mkdirs(); 
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
                getConfig().set(key,value);
                need = true;
            }
        }

        if(need) saveConfig();

    } catch (Exception e) {
      e.printStackTrace();
    } 
    
    if (config.getBoolean("keep_portals_on_stop")) {
      getLogger().info("Loading portals...");
      File file = new File(getDataFolder(), "portal.yml");
      if(file.exists()) {
          try {
              BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
              String l;
              while ((l=br.readLine())!=null) {
                  String[] args = l.split(">");
                  if(args.length!=3) continue;
                  portales.put(args[0], new Portal(args[1].split(","),args[2].split(",")));
              }
              br.close();
          } catch (Exception e) { e.printStackTrace(); }

        cancelPortals(true);
      }
    }

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
    this.all_particles = Boolean.valueOf(config.getBoolean("all_portal_particles"));
    if (this.all_particles.booleanValue()) this.delay = 15; 
    this.tp_log = Boolean.valueOf(config.getBoolean("teleport_log"));
    this.Pportal1 = Particle.valueOf(config.getString("portal1_particle"));
    this.Pportal2 = Particle.valueOf(config.getString("portal2_particle"));
    ROL = Boolean.valueOf(config.getBoolean("remove_on_leave"));
  }

  public Location getGroundBlock(String look, Location loc) {
      //getLogger().info(look);
      Location last = loc.clone();
      if (look.equalsIgnoreCase("N")) {
          last.setX(last.getX() - 1.0D);
          return last;
      } else if (look.equalsIgnoreCase("S")) {
          last.setX(last.getX() + 1.0D);
          return last;
      } else if (look.equalsIgnoreCase("E")) {
          last.setZ(last.getZ() - 1.0D);
          return last;
      } else if (look.equalsIgnoreCase("W")) {
          last.setZ(last.getZ() + 1.0D);
          return last;
      }
      return null;
  }

  public void onDisable() {
	  getLogger().info("Plugin disabled.");
	  if (config.getBoolean("keep_portals_on_stop")) {
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
	  }
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	  Player player = (sender instanceof Player) ? (Player)sender : null;
	  if (cmd.getName().equalsIgnoreCase("portalgun")) {
		  if (player == null) {
			  sender.sendMessage("Don't use this command in console.");
			  return true;
		  }
		  if (args.length >= 1) {
			  if (args[0].equalsIgnoreCase("remove")) {
				  if (player.hasPermission("portalgun.remove")) {
					  cancelPortals(false);
					  if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
						  if (!player.hasPermission("portalgun.remove.all")) {
							  player.sendMessage(prefix+ config.getString("no_permissions"));
							  return true;
						  }
						  portales.clear();
						  sender.sendMessage(ChatColor.RED + config.getString("force_portal_remove"));
					  } else if (portales.containsKey(player.getName())) {
						  portales.remove(player.getName());
						  player.sendMessage(clearPrefix+ ChatColor.GREEN + config.getString("portal_remove"));
					  } else {
						  player.sendMessage(prefix+ config.getString("no_portals"));
					  }
					  cancelPortals(true);
				  } else {
					  player.sendMessage(prefix+ config.getString("no_permissions"));
				  }
			  } else if (args[0].equalsIgnoreCase("?")) {
				  player.sendMessage(clearPrefix);
				  player.sendMessage(ChatColor.GOLD + "/portalgun " + ChatColor.GREEN + "- " + config.getString("info_get_PortalGun"));
				  player.sendMessage(ChatColor.GOLD + "/portalgun remove " + ChatColor.GREEN + "- " + config.getString("info_remove_portals"));
				  player.sendMessage(ChatColor.GOLD + "/portalgun remove all " + ChatColor.GREEN + "- " + config.getString("info_remove_all_portals"));
			  }
		  } else if (player.hasPermission("portalgun.portalgun")) {
              player.getInventory().addItem(new ItemStack[] { this.item });
              if(player.hasPermission("portalgun.boots")) player.getInventory().addItem(new ItemStack[] { botas });
			  player.sendMessage(clearPrefix+ ChatColor.GREEN + config.getString("give_gun"));
		  } else {
			  player.sendMessage(prefix+ config.getString("no_permissions"));
		  }
		  return true;
	  }
	  return false;
  }
  
  public void cancelPortals(boolean open) {
	  for (String ply : portales.keySet()) {
          Portal p = portales.get(ply);
		  try { Bukkit.getServer().getScheduler().cancelTask(((Integer)p.task).intValue()); }
		  catch (Exception exception) {}
		  
		  if (open) {
              //getLogger().info(String.valueOf(Bukkit.getWorld(p.world[0]))+"-"+p.loc[0][0]+"-"+p.loc[0][1]+"-"+p.loc[0][2]);
              Location loc1 = new Location(Bukkit.getServer().getWorld(p.world[0]), p.loc[0][0], p.loc[0][1], p.loc[0][2]);
              //getLogger().info(String.valueOf(loc1.getWorld())+"-"+p.world[0]);
              //getLogger().info(String.valueOf(loc1));
              Location loc2 = new Location(Bukkit.getServer().getWorld(p.world[1]), p.loc[1][0], p.loc[1][1], p.loc[1][2]);
			  if (loc1 != null && loc2 != null) portal(loc1, loc2, String.valueOf(p.dir[0]), String.valueOf(p.dir[1]), p.down[0], p.down[1]);
              p.task=this.tasks;
		  }
	  }
  }
  
  private void portal(final Location loc1, final Location loc2, final String look1, final String look2, final boolean f1, final boolean f2) {
	  this.tasks = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		  public void run() {
			  for (int a = 0; a != 2; a++) {
				  Location loc = loc2;
                  String look = look2;
                  Boolean f = f2;
				  int color = 1;
				  if (a == 0) {
					  loc = loc1;
					  look = look1;
					  f = f1;
					  color = 0;
				  }
				  if (PortalGun.this.all_particles.booleanValue()) {
					  for (int proc = 0; proc != 22; proc++) {
						  PortalGun.this.playParticle(loc, Integer.valueOf(proc), look, f, Integer.valueOf(color));
					  }
				  } else {
					  PortalGun.this.playParticle(loc, Integer.valueOf(PortalGun.this.part_task), look, f, Integer.valueOf(color));
					  PortalGun.this.part_task += 10;
					  if (PortalGun.this.part_task > 21) PortalGun.this.part_task -= 21;
					  PortalGun.this.playParticle(loc, Integer.valueOf(PortalGun.this.part_task), look, f, Integer.valueOf(color));
					  PortalGun.this.part_task++;
					  if (PortalGun.this.part_task > 21) PortalGun.this.part_task -= 21;
				  }
			  }
			  List<Entity> entidades = new ArrayList<Entity>();
			  entidades.addAll(Arrays.asList(loc1.getWorld().getChunkAt(loc1).getEntities()));
			  if(f1) entidades.addAll(Arrays.asList(loc1.getWorld().getChunkAt(getGroundBlock(look1, loc1)).getEntities()));
			  entidades.addAll(Arrays.asList(loc2.getWorld().getChunkAt(loc2).getEntities()));
              if(f2) entidades.addAll(Arrays.asList(loc2.getWorld().getChunkAt(getGroundBlock(look2, loc2)).getEntities()));
			  if (entidades.size() > 0) for (Entity player : entidades) {
				  try {
					  World mundo = player.getLocation().getWorld();
					  double xp = player.getLocation().getBlockX();
					  double yp = player.getLocation().getBlockY();
					  double zp = player.getLocation().getBlockZ();
					  double xloc1 = loc1.getBlockX();
					  //if (xloc1 < 0.0D) xloc1++;
					  double xloc2 = loc2.getBlockX();
					  //if (xloc2 < 0.0D) xloc2++;
					  double zloc1 = loc1.getBlockZ();
					  //if (zloc1 < 0.0D) zloc1++;
					  double zloc2 = loc2.getBlockZ();
					  //if (zloc2 < 0.0D) zloc2++;
					  if (player instanceof Player) {
					      //isPlayerTp((Player) player, loc1, loc2, look1, look2);
					      continue;
					  }
					  if((mundo.equals(loc1.getWorld()) && xp == xloc1 && yp == loc1.getBlockY() && zp == zloc1) ||
                              (f1 && mundo.equals(loc1.getWorld()) && xp == getGroundBlock(look1, loc1).getBlockX() &&
                                      yp == getGroundBlock(look1, loc1).getBlockY() && zp == getGroundBlock(look1, loc1).getBlockZ())) {
						  PortalGun.this.teletransporte(loc2, player, look2, look1, f2, f1);
						  continue;
					  }
					  if (mundo.equals(loc2.getWorld()) && xp == xloc2 && yp == loc2.getBlockY() && zp == zloc2||
                              (f2 && mundo.equals(loc2.getWorld()) && xp == getGroundBlock(look2, loc2).getBlockX() &&
                                      yp == getGroundBlock(look2, loc2).getBlockY() && zp == getGroundBlock(look2, loc2).getBlockZ())) {
						  PortalGun.this.teletransporte(loc1, player, look1, look2, f1, f2);
						  continue;
					  }
					  if (PortalGun.this.entidad_portal.contains(player)) PortalGun.this.entidad_portal.remove(player);
				  } catch (Exception e) { e.printStackTrace(); }
			  }
		  }
	  },this.delay, this.delay);
  }

  private void playParticle(Location loc, int proceso, String look, boolean down, Integer color) {
	  double x = loc.getBlockX();
	  double y = loc.getBlockY();
	  double z = loc.getBlockZ();
      double grado=2*Math.PI*proceso/21;
      double suma = 0.9D*Math.cos(grado);
      double suma2 = 1.8D*Math.sin(grado);

	  if(!down) {
	      y+=suma2;

          if (look.equalsIgnoreCase("E") || look.equalsIgnoreCase("W")) {
              z += 0.1D;
              if (look.equalsIgnoreCase("W"))z += 0.8D;
              x += suma;
          } else {
              x += 0.15D;
              if (look.equalsIgnoreCase("S")) x += 0.75D;
              z += suma;
          }
      }
	  else {
	      y+=0.1D;
          if (look.equalsIgnoreCase("E") || look.equalsIgnoreCase("W")) {
              x+=suma;
              z+=suma2;
              if(look.equalsIgnoreCase("E")) z-=1;
          }
          else {
              x+=suma2;
              if(look.equalsIgnoreCase("N")) x-=1;
              z+=suma;
          }
      }
	  Particle particula = this.Pportal2;
	  if (color.intValue() == 0) particula = this.Pportal1;
      //getLogger().info(String.valueOf(loc.getWorld()));
	  loc.getWorld().spawnParticle(particula, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
	}
  
  public void teletransporte(Location loc, Entity player, String look, String lastL, boolean down, boolean lastD) {
	  if (!this.entidad_portal.contains(player)) { this.entidad_portal.add(player); }
	  else { return; }
	  
	  if (loc.getWorld().getPlayers().size() == 0 && !(player instanceof Player)) return;
	  if (!player.getPassengers().isEmpty() && player.getPassengers().get(0) instanceof Player) player = (Entity)player.getPassengers().get(0);
	  if (this.tp_log.booleanValue()) getLogger().info("Teleporting " + player.getName() + "...");

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
  }
  
  public static String getCardinalDirection(Player player) {
	  double rotation = ((player.getLocation().getYaw() - 90.0F) % 360.0F);
  
  if (rotation < 0.0D) {
      rotation += 360.0D;
    }
    
    if (0.0D <= rotation && rotation < 22.5D)
      return "N"; 
    if (22.5D <= rotation && rotation < 67.5D) {
        /*if (rotation - 22.5D <= 22.5D) return "N";
        else return "E";*/
        return "NE";
    }
    if (67.5D <= rotation && rotation < 112.5D)
      return "E";
    if (112.5D <= rotation && rotation < 157.5D) {
        /*if (rotation - 112.5D <= 22.5D) return "E";
        else return "S";*/
        return "SE";
    }
    if (157.5D <= rotation && rotation < 202.5D)
      return "S"; 
    if (202.5D <= rotation && rotation < 247.5D) {
        /*if (rotation - 202.5D <= 22.5D) return "S";
        else return "W";*/
        return "SW";
    }
    if (247.5D <= rotation && rotation < 292.5D)
      return "W"; 
    if (292.5D <= rotation && rotation < 337.5D) {
        /*if (rotation - 292.5D <= 22.5D) return "W";
        else return "N";*/
        return "NW";
    }
    if (337.5D <= rotation && rotation < 360.0D) return "N";
    return null;
 }

}

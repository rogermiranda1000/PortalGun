package com.rogermiranda1000.portalgun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
  List<Entity> entidad_portal = new ArrayList<Entity>();
  
  int tasks;
    ItemStack item;
    ItemStack botas;
  Boolean all_particles;
  Boolean tp_log;
  int part_task = 0;
  int delay = 2;
  public static final String clearPrefix=ChatColor.GOLD+""+ChatColor.BOLD+"[PortalGun] ",prefix=clearPrefix+ChatColor.RED;

  Particle Pportal1;
  Particle Pportal2;
  Boolean ROL;
  
  public void onEnable() {
    getLogger().info("Plugin activated.");

      instancia = this;

    config = getConfig();
    try {
      if (!getDataFolder().exists()) getDataFolder().mkdirs(); 
      File file = new File(getDataFolder(), "config.yml");
      if (!file.exists()) {
        getLogger().info("Creating config.yml...");
        file.createNewFile();
        
        getConfig().set("portal_denied", "You can't open a portal here.");
        getConfig().set("no_permissions", "You don't have permissions to do this.");
        getConfig().set("half_portal_opened", "Portal opened at [pos].");
        getConfig().set("portal_opened", "The portals have been linked.");
        getConfig().set("portal_failed", "Error at opening the portal!");
        getConfig().set("no_portals", "You don't have any opened portals right now.");
        getConfig().set("same_portal", "You can't place both portals at the same block!");
          getConfig().set("portal_remove", "You deleted successfully your portals.");
          getConfig().set("portal_removed_by_death", "Your portals have been deleted due to your death.");
          getConfig().set("portal_removed_by_world_change", "You can't keep portals between worlds.");
        getConfig().set("force_portal_remove", "Deleted all portals.");
        getConfig().set("give_gun", "PortalGun gived!");
        getConfig().set("info_get_PortalGun", "Get your PortalGun.");
        getConfig().set("info_remove_portals", "Delete your active portals.");
        getConfig().set("info_remove_all_portals", "Delete all the active portals.");
        getConfig().set("destroy_portal", "You have destroyed [player]'s portal.");
        getConfig().set("your_portal_destroyed", "Your portal has been destroyed by [player].");
        getConfig().set("denied_block", "You can't open a portal in that block.");
        getConfig().set("max_portal_length", Integer.valueOf(80));
        getConfig().set("all_portal_particles", Boolean.valueOf(false));
        getConfig().set("teleport_log", Boolean.valueOf(true));
        getConfig().set("portalgun_material", "BLAZE_ROD");
        getConfig().set("portal1_particle", "FLAME");
        getConfig().set("portal2_particle", "VILLAGER_HAPPY");
        getConfig().set("remove_on_leave", Boolean.valueOf(true));
        getConfig().set("keep_portals_on_stop", Boolean.valueOf(false));
        getConfig().set("delete_portals_on_death", Boolean.valueOf(false));
          getConfig().set("only_certain_blocks", Boolean.valueOf(false));
          getConfig().set("use_only_your_portals", Boolean.valueOf(false));
          getConfig().set("remove_portals_on_world_change", Boolean.valueOf(false));
        getConfig().set("blocks", "QUARTZ_BLOCK");
        saveConfig();
      } else {
        if (!getConfig().isSet("destroy_portal")) {
          getConfig().set("destroy_portal", "You have destroyed [player]'s portal.");
          saveConfig();
        }
        if (!getConfig().isSet("your_portal_destroyed")) {
            getConfig().set("your_portal_destroyed", "Your portal has been destroyed by [player].");
            saveConfig();
          }
        if (!getConfig().isSet("denied_block")) {
            getConfig().set("denied_block", "You can't open a portal in that block.");
            saveConfig();
          }
        if (!getConfig().isSet("keep_portals_on_stop")) {
            getConfig().set("keep_portals_on_stop", Boolean.valueOf(false));
            saveConfig();
          }
        if (!getConfig().isSet("delete_portals_on_death")) {
            getConfig().set("delete_portals_on_death", Boolean.valueOf(false));
            saveConfig();
          }
          if (!getConfig().isSet("only_certain_blocks")) {
              getConfig().set("only_certain_blocks", Boolean.valueOf(false));
              saveConfig();
          }
          if (!getConfig().isSet("use_only_your_portals")) {
              getConfig().set("use_only_your_portals", Boolean.valueOf(false));
              saveConfig();
          }
          if (!getConfig().isSet("remove_portals_on_world_change")) {
              getConfig().set("remove_portals_on_world_change", Boolean.valueOf(false));
              saveConfig();
          }
          if (!getConfig().isSet("portal_removed_by_death")) {
              getConfig().set("portal_removed_by_death", "Your portals have been deleted due to your death.");
              saveConfig();
          }
          if (!getConfig().isSet("portal_removed_by_world_change")) {
              getConfig().set("portal_removed_by_world_change", "You can't keep portals between worlds.");
              saveConfig();
          }
        if (!getConfig().isSet("blocks")) {
            getConfig().set("blocks", "QUARTZ_BLOCK");
            saveConfig();
          }
      }
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
    
    final int max_length = config.getInt("max_portal_length");
    this.all_particles = Boolean.valueOf(config.getBoolean("all_portal_particles"));
    if (this.all_particles.booleanValue()) this.delay = 15; 
    this.tp_log = Boolean.valueOf(config.getBoolean("teleport_log"));
    this.Pportal1 = Particle.valueOf(config.getString("portal1_particle"));
    this.Pportal2 = Particle.valueOf(config.getString("portal2_particle"));
    this.ROL = Boolean.valueOf(config.getBoolean("remove_on_leave"));
    
    getServer().getPluginManager().registerEvents(new Listener() {
          @EventHandler
          //public abstract void onLeave(PlayerQuitEvent event);
          public void onLeave(PlayerQuitEvent event) {
            if (!PortalGun.this.ROL.booleanValue())
              return; 
            String nick = event.getPlayer().getName();
            if (!portales.containsKey(nick)) return;
            //if (!PortalGun.this.portal2.containsKey(nick)) return;
            PortalGun.this.cancelPortals(false);
            portales.remove(nick);
            //PortalGun.this.portal2.remove(nick);
            PortalGun.this.cancelPortals(true);
          }

          @EventHandler(ignoreCancelled = true)
          public void onTabCompleteEvent(TabCompleteEvent e) {
            if (e.getBuffer().equals("/portalgun ")) {
            	e.setCompletions(Arrays.asList(new String[] { "?", "remove", "remove all" }));
            }
            else if (e.getBuffer().equals("/portalgun remove ")) {
            	e.setCompletions(Arrays.asList(new String[] { "all" })); 
            }
          }
          
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
          


          @EventHandler
          public void onMove(PlayerMoveEvent e) {
              /*if (e.getFrom().getWorld()==e.getTo().getWorld() && e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getTo().getBlockZ())
                  return;*/
              Player player = e.getPlayer();
              Location loc = e.getTo().getBlock().getLocation();
                if(player.getInventory().getBoots()!=null && player.getInventory().getBoots().equals(botas)) player.setFallDistance(0);
              for (String s : portales.keySet()) {
                  Portal p = portales.get(s);
                  if(p.world[0]=="" || p.world[1]=="") continue;
                  double l[] = {loc.getX(), loc.getY(), loc.getZ()};
                int result = p.contains(l,loc.getWorld().getName());
                double temp[] = {e.getFrom().getBlock().getLocation().getX(),e.getFrom().getBlock().getLocation().getY(),e.getFrom().getBlock().getLocation().getZ()};
                if(result==-1) continue;
                  if(p.down[result] && result==p.contains(temp,loc.getWorld().getName())) continue;
                if(config.getBoolean("use_only_your_portals") && !player.hasPermission("portalgun.overrideotherportals") && player.getName()!=s) continue;
                  if (entidad_portal.contains(player)) entidad_portal.remove(player);
                String Nlooking = getCardinalDirection(player);
                if(!Nlooking.equalsIgnoreCase(String.valueOf(Nlooking.charAt(0)))) continue;

                int op = 0;
                if(result==0) op = 1;
                if(p.dir[result]==Nlooking.charAt(0) || p.down[result])
                    teletransporte(new Location(Bukkit.getServer().getWorld(p.world[op]), p.loc[op][0], p.loc[op][1], p.loc[op][2]), player,
                            String.valueOf(p.dir[op]), String.valueOf(p.dir[result]), p.down[op], p.down[result]);
               }
          }

          @EventHandler
          public void onWorldChange(PlayerChangedWorldEvent e) {
              Player player = e.getPlayer();
              if (!PortalGun.config.getBoolean("remove_portals_on_world_change") || !portales.containsKey(player.getName())) return;
              cancelPortals(false);
              portales.remove(player.getName());
              cancelPortals(true);
              player.sendMessage(prefix+ PortalGun.config.getString("portal_removed_by_world_change"));
          }

          
          @EventHandler
          public void onPlayerUse(PlayerInteractEvent event) {
            Player player = event.getPlayer();
            
            if (!player.getInventory().getItemInMainHand().equals(PortalGun.this.item) && !player.getInventory().getItemInOffHand().equals(PortalGun.this.item)) return;
            if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
              event.setCancelled(true);
              if (!player.hasPermission("portalgun.open")) {
                player.sendMessage(prefix+ PortalGun.config.getString("no_permissions"));
                return;
              } 
              BlockIterator iter = new BlockIterator(player, max_length);
              Block lastBlock = iter.next();
              while (iter.hasNext()) {
                lastBlock = iter.next();
                if (lastBlock.getType() == Material.AIR) {
                  continue;
                }
                break;
              }
              Location block = lastBlock.getLocation();
              Location last = new Location(block.getWorld(), block.getX(), block.getY() + 1.0D, block.getZ());
              boolean ground = false;
              
              if (!lastBlock.getType().isSolid()) {
                player.sendMessage(prefix+ PortalGun.config.getString("portal_denied"));
                return;
              }
                List<String> b = Arrays.asList(PortalGun.config.getString("blocks").replace(" ", "").split(","));
                String looking = getCardinalDirection(player);
              if(last.getBlock().getType()==Material.AIR) {
                  //Portal en el suelo
                  double x = 0D;
                  double z = 0D;
                  last.setY(last.getY()-1D);
                  last = getGroundBlock(looking, last);
                   if(last==null) {
                      player.sendMessage(prefix+ PortalGun.config.getString("portal_failed"));
                      return;
                  }
                  if(!last.getBlock().getType().isSolid()) {
                      player.sendMessage(prefix+ PortalGun.config.getString("portal_denied"));
                      return;
                  }
                  ground = true;
              }
              if (!player.hasPermission("portalgun.overrideblocks") && (!b.contains(String.valueOf(lastBlock.getType()).replace("LEGACY_","")) ||
            		  !b.contains(String.valueOf(last.getBlock().getType()).replace("LEGACY_","")))) {
                player.sendMessage(prefix+ PortalGun.config.getString("denied_block"));
                  //player.sendMessage(String.valueOf(lastBlock.getType())+" - "+String.valueOf(b));
                return;
              }
              
              if(!ground) {
                  if (looking.equalsIgnoreCase("N")) {
                      block.setX(block.getX() + 1.0D);
                  } else if (looking.equalsIgnoreCase("S")) {
                      block.setX(block.getX() - 1.0D);
                  } else if (looking.equalsIgnoreCase("E")) {
                      block.setZ(block.getZ() + 1.0D);
                  } else if (looking.equalsIgnoreCase("W")) {
                      block.setZ(block.getZ() - 1.0D);
                  } else {
                      player.sendMessage(prefix+ PortalGun.config.getString("portal_failed"));
                      return;
                  }
              } else {
                  block.setY(block.getY()+1);
                  last.setY(last.getY()+1);
              }

              Location block2 = new Location(block.getWorld(), block.getX(), block.getY() + 1.0D, block.getZ());
              if (block.getBlock().getType() != Material.AIR || (block2.getBlock().getType() != Material.AIR && !ground) ||
                      (ground && last.getBlock().getType()!=Material.AIR)) {
            	  player.sendMessage(prefix+ PortalGun.config.getString("portal_denied"));
            	  return;
              }

              Location loc1 = null;
              Location loc2 = null;
                if(!portales.containsKey(player.getName())) portales.put(player.getName(), new Portal());

                Portal p = portales.get(player.getName());
                if(p.world[0]!="") loc1 = new Location(Bukkit.getServer().getWorld(p.world[0]), p.loc[0][0], p.loc[0][1], p.loc[0][2]);
                if(p.world[1]!="") loc2 = new Location(Bukkit.getServer().getWorld(p.world[1]), p.loc[1][0], p.loc[1][1], p.loc[1][2]);
              if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) { loc1 = block; }
              else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) { loc2 = block; } 
              if (loc1 != null && loc2 != null && (loc1.equals(loc2) || (new Location(loc1.getWorld(), loc1.getX(), loc1.getY() + 1.0D, loc1.getZ())).equals(loc2) ||
            		  (new Location(loc1.getWorld(), loc1.getX(), loc1.getY() - 1.0D, loc1.getZ())).equals(loc2))) {
            	  player.sendMessage(prefix+ PortalGun.config.getString("same_portal"));
            	  return;
              }
              if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                  p.loc[0][0] = block.getX();
                  p.loc[0][1] = block.getY();
                  p.loc[0][2] = block.getZ();
                  p.dir[0] = looking.charAt(0);
                  p.world[0] = block.getWorld().getName();
                  p.down[0] = ground;
                  //getLogger().info(String.valueOf(block.getWorld())+"-"+p.world[0]);
                  loc1 = new Location(Bukkit.getServer().getWorld(p.world[0]), p.loc[0][0], p.loc[0][1], p.loc[0][2]);
              } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                  p.loc[1][0] = block.getX();
                  p.loc[1][1] = block.getY();
                  p.loc[1][2] = block.getZ();
                  p.dir[1] = looking.charAt(0);
                  p.world[1] = block.getWorld().getName();
                  p.down[1] = ground;
                  loc2 = new Location(Bukkit.getServer().getWorld(p.world[1]), p.loc[1][0], p.loc[1][1], p.loc[1][2]);
                  //getLogger().info(String.valueOf(Bukkit.getServer().getWorld(p.world[1]))+"-"+String.valueOf(loc2.getWorld()));
              }
              if (loc1 != null && loc2 != null) { 
            	  player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 3.0F, 0.5F);
            	  PortalGun.this.getLogger().info(String.valueOf(player.getName()) + " >> " + block.getBlockX() + ", " + block.getBlockY() + ", " + block.getBlockZ() + " (" + looking + ")");
            	  String message = PortalGun.config.getString("half_portal_opened").replace("[pos]", String.valueOf(block.getX()) + ", " + block.getY() + ", " + block.getZ());
            	  player.sendMessage(clearPrefix + ChatColor.GREEN + message);
            	  PortalGun.this.getLogger().info(String.valueOf(player.getName()) + " has opened a portal.");
            	  player.sendMessage(clearPrefix + ChatColor.GREEN + PortalGun.config.getString("portal_opened"));
            	  PortalGun.this.cancelPortals(true);
              } else {
            	  player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 3.0F, 0.5F);
            	  PortalGun.this.getLogger().info(String.valueOf(player.getName()) + " >> " + block.getBlockX() + ", " + block.getBlockY() + ", " + block.getBlockZ() + " (" + looking + ")");
            	  String message = PortalGun.config.getString("half_portal_opened").replace("[pos]", block.getX() + ", " + block.getY() + ", " + block.getZ());
            	  player.sendMessage(clearPrefix + ChatColor.GREEN + message);
              }
          }
      },this);
  }

  Location getGroundBlock(String look, Location loc) {
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
		  /*for(int a = 0; a<2; a++) {
              try {
                  File file = new File(getDataFolder(), "portal"+String.valueOf(a+1)+".yml");
                  BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                  for (String n : portales.keySet()) {
                      Portal p = portales.get(n);
                      Location l = new Location(Bukkit.getServer().getWorld(p.world[a]), p.loc[a][0], p.loc[a][1], p.loc[a][2]);
                      bw.write(n + ">" + String.valueOf(l));
                      bw.newLine();
                  }
                  bw.flush();
                  bw.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
          for(int a = 0; a<2; a++) {
              try {
                  File file = new File(getDataFolder(), "portal"+String.valueOf(a+1)+"L.yml");
                  BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                  for (String p : portales.keySet()) {
                      bw.write(String.valueOf(p) + ">" + portales.get(p).dir[a]);
                      bw.newLine();
                  }
                  bw.flush();
                  bw.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }*/
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
              player.getInventory().addItem(new ItemStack[] { botas });
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

      /*if (proceso.intValue() == 0) { suma = 0.5D; }
	  else if (proceso.intValue() == 1) { suma = 0.6D; suma2 += 0.1D; }
	  else if (proceso.intValue() == 2) { suma = 0.7D; suma2 += 0.25D; }
	  else if (proceso.intValue() == 3) { suma = 0.8D; suma2 += 0.4D; }
	  else if (proceso.intValue() == 4) { suma = 0.9D; suma2 += 0.6D; }
	  else if (proceso.intValue() == 5) { suma = 0.9D; suma2 += 0.8D; }
	  else if (proceso.intValue() == 6) { suma = 0.9D; suma2++; }
	  else if (proceso.intValue() == 7) { suma = 0.9D; suma2 += 1.2D; }
	  else if (proceso.intValue() == 8) { suma = 0.8D; suma2 += 1.4D; }
	  else if (proceso.intValue() == 9) { suma = 0.7D; suma2 += 1.6D; }
	  else if (proceso.intValue() == 10) { suma = 0.6D; suma2 += 1.8D; }
	  else if (proceso.intValue() == 11) { suma = 0.5D; suma2 += 1.8D; }
	  else if (proceso.intValue() == 12) { suma = 0.4D; suma2 += 1.8D; }
	  else if (proceso.intValue() == 13) { suma = 0.3D; suma2 += 1.6D; }
	  else if (proceso.intValue() == 14) { suma = 0.2D; suma2 += 1.4D; }
	  else if (proceso.intValue() == 15) { suma = 0.1D; suma2 += 1.2D; }
	  else if (proceso.intValue() == 16) { suma = 0.1D; suma2++; }
	  else if (proceso.intValue() == 17) { suma = 0.1D; suma2 += 0.8D; }
	  else if (proceso.intValue() == 18) { suma = 0.1D; suma2 += 0.6D; }
	  else if (proceso.intValue() == 19) { suma = 0.2D; suma2 += 0.4D; }
	  else if (proceso.intValue() == 20) { suma = 0.3D; suma2 += 0.25D; }
	  else if (proceso.intValue() == 21) { suma = 0.4D; suma2 += 0.1D; }*/

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
  
  private void teletransporte(Location loc, Entity player, String look, String lastL, boolean down, boolean lastD) {
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
  
  private static String getCardinalDirection(Player player) {
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

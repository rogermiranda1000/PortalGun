package com.rogermiranda1000.portalgun;

import java.io.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import com.rogermiranda1000.portalgun.eventos.*;
import com.rogermiranda1000.portalgun.files.Config;
import com.rogermiranda1000.portalgun.files.FileManager;
import com.rogermiranda1000.portalgun.portals.CeilingPortal;
import com.rogermiranda1000.portalgun.portals.FloorPortal;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.portals.WallPortal;
import com.rogermiranda1000.portalgun.versioncontroller.VersionController;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class PortalGun extends JavaPlugin
{
    public static PortalGun plugin;

    public static ItemStack item;
    public static ItemStack botas;
    public static final String clearPrefix=ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "[PortalGun] " + ChatColor.GREEN.toString(), errorPrefix=clearPrefix+ChatColor.RED;
    private static final int particleDelay = 2;
    private static final HashMap<Entity, Location> teleportedEntities = new HashMap<>();

    private BukkitTask particleTask;
    private BukkitTask teleportTask;

    @Override
    public void onEnable() {
        PortalGun.plugin = this;

        FileManager.loadFiles();

        // Load portals
        if (Config.PERSISTANT.getBoolean()) {
            getLogger().info("Loading portals...");
            File file = new File(getDataFolder(), "portals.yml");
            if(file.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String l;
                    while ((l=br.readLine())!=null) {
                        String[] args = l.split(";");
                        if(args.length!=5) continue;
                        String[] argsWorld = args[1].split(",");
                        if(argsWorld.length!=4) continue;
                        World w = Bukkit.getWorld(argsWorld[0]);
                        if (w == null) {
                            PortalGun.printErrorMessage("The portal's world '" + argsWorld[0] + "' doesn't exist.");
                            continue;
                        }
                        Location portalLocation = new Location(w, Double.parseDouble(argsWorld[1]), Double.parseDouble(argsWorld[2]), Double.parseDouble(argsWorld[3]));
                        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
                        Portal p = null;
                        switch (args[4]) {
                            case "CeilingPortal":
                                p = new CeilingPortal(player, portalLocation, Direction.valueOf(args[2]), args[3].equalsIgnoreCase("L"));
                                break;
                            case "FloorPortal":
                                p = new FloorPortal(player, portalLocation, Direction.valueOf(args[2]), args[3].equalsIgnoreCase("L"));
                                break;
                            case "WallPortal":
                                p = new WallPortal(player, portalLocation, Direction.valueOf(args[2]), args[3].equalsIgnoreCase("L"));
                                break;
                            default:
                                PortalGun.printErrorMessage("Invalid portal type (" + args[4] + ")");
                        }
                        if (p != null) Portal.setPortal(UUID.fromString(args[0]), p);
                    }
                    br.close();
                } catch (Exception e) { e.printStackTrace(); }
            }
            else getLogger().info("No portals to load.");
        }

        botas = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) botas.getItemMeta();
        meta2.setDisplayName(ChatColor.GREEN.toString() + "Portal Boots");
        // TODO: unbreakable alternative
        if (VersionController.getVersion() > 10) meta2.setUnbreakable(true);
        meta2.setColor(Color.WHITE);
        botas.setItemMeta(meta2);
        botas.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    
        // Particles
        this.particleTask = this.getServer().getScheduler().runTaskTimerAsynchronously(this, PortalGun::playAllParticles, 0, PortalGun.particleDelay);
        // TODO: configuration "only players teleports"
        // Entities
        this.teleportTask = this.getServer().getScheduler().runTaskTimerAsynchronously(this, ()->{
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
            for (Entity e : getEntities(world)) {
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
                    try {
                        // Async does not support teleport between worlds
                        Bukkit.getScheduler().callSyncMethod(PortalGun.plugin, () -> {
                            if (portal.teleportToDestiny(e, destinyLocation))
                                PortalGun.teleportedEntities.put(e, destinyLocation);
                            return null;
                        });
                    } catch (CancellationException ex) {}
                }
            }
        }
    }

    private static List<Entity> getEntities(World world){
        //if(Bukkit.isPrimaryThread()){
            return world.getEntities();
        /*}else{
            try{
                return Bukkit.getScheduler().callSyncMethod(PortalGun.plugin, world::getEntities).get();
            }catch(InterruptedException|ExecutionException Ex){
                return new ArrayList<>(0);
            }
        }*/
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
        this.particleTask.cancel();
        this.teleportTask.cancel();

        if (Config.PERSISTANT.getBoolean()) {
            getLogger().info("Saving portals...");
            File file = new File(getDataFolder(), "portals.yml");
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
                for (Portal p : Portal.getPortals()) {
                    bw.write(p.toString());
                    bw.newLine();
                }
            }
            catch (IOException e) { e.printStackTrace(); }
            finally {
                if (bw != null) {
                    try { bw.close(); }
                    catch (IOException e) { e.printStackTrace(); }
                }
            }
        }
    }
}

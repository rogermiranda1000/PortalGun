package com.rogermiranda1000.portalgun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitTask;

import com.rogermiranda1000.helper.RogerPlugin;
import com.rogermiranda1000.helper.SentryScheduler;
import com.rogermiranda1000.portalgun.blocks.ResetBlocks;
import com.rogermiranda1000.portalgun.events.onDead;
import com.rogermiranda1000.portalgun.events.onLeave;
import com.rogermiranda1000.portalgun.events.onMove;
import com.rogermiranda1000.portalgun.events.onPlayerDamagesEntity;
import com.rogermiranda1000.portalgun.events.onPlayerJoin;
import com.rogermiranda1000.portalgun.events.onPortalgunEntity;
import com.rogermiranda1000.portalgun.events.onUse;
import com.rogermiranda1000.portalgun.files.Config;
import com.rogermiranda1000.portalgun.files.FileManager;
import com.rogermiranda1000.portalgun.files.Language;
import com.rogermiranda1000.portalgun.portals.CeilingPortal;
import com.rogermiranda1000.portalgun.portals.FloorPortal;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.portalgun.portals.WallPortal;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;

import net.md_5.bungee.api.ChatColor;

public class PortalGun extends RogerPlugin {
    public static PortalGun plugin;

    public static boolean useResourcePack, takeEntities;
    public static ItemStack item;
    public static ItemStack botas;
    private static final int particleDelay = 2, pickedEntitiesDelay = 3;
    public static final HashMap<Entity, Location> teleportedEntities = new HashMap<>();
    public static final Set<String> entityTeleportBlacklist = new HashSet<>();
    public static final int MAX_ENTITY_PICK_RANGE = 4; // TODO configurable

    private BukkitTask particleTask;
    private BukkitTask teleportTask;
    private BukkitTask pickEntitiesTask;

    public PortalGun() {
        super(new onDead(), new onLeave(), new onMove(), new onUse(new onPortalgunEntity()), new onPlayerJoin(), new onPlayerDamagesEntity());

        this.addCustomBlock(ResetBlocks.setInstance(new ResetBlocks(this)));
    }

    @Override
    public String getPluginID() {
        return "44746";
    }

    @Override
    public Integer getMetricsID() {
        // /!\\ Other branches MUST change this to null (or your ID) /!\\
        return 15938;
    }

    @Override
    public String getSentryDsn() {
        // /!\\ Other branches MUST change this to null (or your Dsn) /!\\
        return "https://26fe0afe50a948c6a86b68205315fcc5@o1339981.ingest.sentry.io/6612626";
    }

    @Override
    public void preOnEnable() {
        PortalGun.plugin = this;

        FileManager.loadFiles();
        this.setCommandMessages(Language.USER_NO_PERMISSIONS.getText(), Language.HELP_UNKNOWN.getText());

        super.setCommands(PortalGunCommands.commands); // @pre before super.onEnable() & after loading languages
    }
    @Override
    public void postOnEnable() {
        ResetBlocks.getInstance().updateAllBlocks(); // @pre super.onEnable()

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
                            this.printConsoleErrorMessage("The portal's world '" + argsWorld[0] + "' doesn't exist.");
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
                                this.printConsoleErrorMessage("Invalid portal type (" + args[4] + ")");
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
        if (VersionController.version.compareTo(Version.MC_1_10) > 0) meta2.setUnbreakable(true);
        meta2.setColor(Color.WHITE);
        botas.setItemMeta(meta2);
        botas.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        SentryScheduler scheduler = new SentryScheduler(this);
        // Particles
        this.particleTask = scheduler.runTaskTimer(this, PortalGun::playAllParticles, 0, PortalGun.particleDelay);
        // TODO: configuration "only players teleports"
        // Entities
        this.teleportTask = scheduler.runTaskTimerAsynchronously(this, ()->{
            PortalGun.updateTeleportedEntities();
            PortalGun.teleportEntities();
        }, 1, PortalGun.particleDelay*3);
        this.pickEntitiesTask = scheduler.runTaskTimerAsynchronously(this, onPortalgunEntity::updatePickedEntities, 0, PortalGun.pickedEntitiesDelay);
    }

    private static void playAllParticles() {
        for (Portal p: Portal.getPortals()) {
            if (p.getPosition().getChunk().isLoaded()) p.playParticle();
        }

        ResetBlocks.getInstance().playAllParticles();
    }

    // TODO: don't teleport Item Frames
    private static void teleportEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity e : getEntities(world)) {
                if (e instanceof Player) continue;
                synchronized (PortalGun.teleportedEntities) {
                    if (PortalGun.teleportedEntities.containsKey(e)) continue;
                }
                synchronized (PortalGun.entityTeleportBlacklist) {
                    if (PortalGun.entityTeleportBlacklist.contains(e.getType().getName())) continue;
                }

                final Location entityBlockLocation = e.getLocation().getBlock().getLocation();
                final Portal portal = Portal.getPortal(entityBlockLocation);
                if (portal == null) continue;

                // TODO: horses
                final Location destinyLocation = portal.getDestiny(portal.getLocationIndex(entityBlockLocation));
                if (destinyLocation == null) continue;

                Bukkit.getScheduler().callSyncMethod(PortalGun.plugin, () -> {
                    if (portal.teleportToDestiny(e, VersionController.get().getVelocity(e), destinyLocation)) {
                        synchronized (PortalGun.teleportedEntities) {
                            PortalGun.teleportedEntities.put(e, destinyLocation);
                        }
                    }
                    return null;
                });
            }
        }
    }

    private static List<Entity> getEntities(World world){
        if (Bukkit.isPrimaryThread()) {
            return world.getEntities();
        }
        else{
            try {
                return Bukkit.getScheduler().callSyncMethod(PortalGun.plugin, world::getEntities).get();
            }
            catch(Exception ignore) {
                return new ArrayList<>(0);
            }
        }
    }

    /**
     * sees if teleported entities has moved (therefore, must be removed from the list)
     */
    private static void updateTeleportedEntities() {
        synchronized (PortalGun.teleportedEntities) {
            PortalGun.teleportedEntities.entrySet().removeIf(e -> !e.getKey().isValid()); // Entity no loger exists
            PortalGun.teleportedEntities.entrySet().removeIf(e -> !e.getKey().getLocation().getBlock().getLocation().equals(e.getValue())
                    && (Portal.getPortal(e.getValue()) == null || !Portal.getPortal(e.getValue()).equals(Portal.getPortal(e.getKey().getLocation().getBlock().getLocation())))); // Entity has moved to another portal (or no portal at all)
        }
    }

    @Override
    public void postOnDisable() {
        this.particleTask.cancel();
        this.teleportTask.cancel();
        this.pickEntitiesTask.cancel();

        onPortalgunEntity.clear();

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

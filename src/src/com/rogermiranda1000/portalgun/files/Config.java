package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.versioncontroller.VersionController;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

// TODO: config
public enum Config {
    LANGUAGE("language"),
    MATERIAL("portalgun_material"),
    DELETE_ON_DEATH("portals.remove_on_death"),
    REMOVE_ON_LEAVE("portals.remove_on_leave"),
    MAX_LENGHT("portals.placement_length"),
    WHITELIST_BLOCKS("portals.whitelist_blocks"),
    WHITELISTED_BLOCKS("portals.whitelisted_blocks"),
    ONLY_YOUR_PORTALS("portals.use_only_yours"),
    PERSISTANT("portals.save"),
    PARTICLES("portals.particles");

    private static FileConfiguration fileConfiguration;
    private static HashMap<Config, Object> savedConfiguration;
    private final String key;

    Config(String key) {
        this.key = key;
    }

    private Object getObject() {
        Object r = Config.savedConfiguration.get(this);

        // first time
        if (r == null) {
            r = Config.fileConfiguration.get(this.key);
            Config.savedConfiguration.put(this, r);
        }

        return r;
    }

    public boolean getBoolean() {
        return (boolean)this.getObject();
    }

    public int getInteger() {
        return (int)this.getObject();
    }

    public static void loadConfig() {
        Config.loadValidBlocks();

        Config.loadPortalgunMaterial(Config.fileConfiguration.getString(MATERIAL.key));

        Language.loadHashMap(Config.fileConfiguration.getString(LANGUAGE.key));

        loadPortalParticles();
    }

    public static void checkAndCreate() {
        Config.savedConfiguration = new HashMap<>();
        Config.fileConfiguration = PortalGun.plugin.getConfig();

        File configFile = new File(FileManager.pluginFolder, "config.yml");
        if (!configFile.exists()) {
            PortalGun.plugin.getLogger().info("Configuration file not found, creating a new one...");
            try {
                configFile.createNewFile();
                for (Map.Entry<String, Object> e : getDefaultConfiguration().entrySet()) Config.fileConfiguration.set(e.getKey(), e.getValue());
                PortalGun.plugin.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: 1.8 Effect (instead of Particle)
    private static void loadPortalParticles() {
        List<String> particles = Config.fileConfiguration.getStringList(Config.PARTICLES.key);
        if (particles.size() != 2) {
            PortalGun.printErrorMessage(Config.PARTICLES.key + " must have only 2 particles!");
            return;
        }

        try {
            Portal.setParticle(Particle.valueOf(particles.get(0)), true);
        } catch (IllegalArgumentException IAEx) {
            PortalGun.printErrorMessage("Particle '" + particles.get(0) + "' does not exists.");
        }

        try {
            Portal.setParticle(Particle.valueOf(particles.get(1)), false);
        } catch (IllegalArgumentException IAEx) {
            PortalGun.printErrorMessage("Particle '" + particles.get(1) + "' does not exists.");
        }
    }

    private static void loadPortalgunMaterial(@Nullable String material) {
        if (material == null) {
            PortalGun.printErrorMessage(MATERIAL.key + " is not setted in config file!");
            return;
        }

        Material portalgunMaterial = Material.getMaterial(material);
        if (portalgunMaterial == null) {
            PortalGun.printErrorMessage("PortalGun's item (" + material + ") does not exists.");
            return;
        }

        PortalGun.item = new ItemStack(portalgunMaterial);
        ItemMeta meta = PortalGun.item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "PortalGun");
        PortalGun.item.setItemMeta(meta);
        PortalGun.item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    }

    private static void loadValidBlocks() {
        ArrayList<Object> allowedBlocks = new ArrayList<>();

        for (String txt : Config.fileConfiguration.getStringList(Config.WHITELISTED_BLOCKS.key)) {
            Object o = VersionController.get().getMaterial(txt);
            if (o != null) allowedBlocks.add(o);
        }

        // TODO: lava restriction?
        // TODO: isPassable?
        Portal.isEmptyBlock = VersionController.get()::isPassable;
        Portal.isValidBlock = b->( !VersionController.get().isPassable(b) && (!Config.fileConfiguration.getBoolean(Config.WHITELIST_BLOCKS.key) || allowedBlocks.contains(VersionController.get().getObject(b))) );
    }

    private static HashMap<String,Object> getDefaultConfiguration() {
        HashMap<String,Object> c = new HashMap<>();

        c.put(Config.LANGUAGE.key, "english");
        c.put(Config.MATERIAL.key, "BLAZE_ROD");
        c.put(Config.MAX_LENGHT.key, 80);
        c.put(Config.PARTICLES.key, Arrays.asList("FLAME", "VILLAGER_HAPPY"));
        c.put(Config.REMOVE_ON_LEAVE.key, true);
        c.put(Config.DELETE_ON_DEATH.key, false);
        c.put(Config.PERSISTANT.key, false);
        c.put(Config.ONLY_YOUR_PORTALS.key, false);
        c.put(Config.WHITELIST_BLOCKS.key, false);
        c.put(Config.WHITELISTED_BLOCKS.key, getDefaultBlocks());

        return c;
    }

    private static ArrayList<String> getDefaultBlocks() {
        ArrayList<String> blocks = new ArrayList<>();

        if (VersionController.getVersion()<13) {
            blocks.add("WOOL:0");
            blocks.add("QUARTZ_BLOCK:0");
            blocks.add("QUARTZ_BLOCK:1");
            blocks.add("QUARTZ_BLOCK:2");
            //blocks.add("CONCRETE:0");
        }
        else {
            blocks.add("WHITE_WOOL");
            blocks.add("QUARTZ_BLOCK");
            blocks.add("CHISELED_QUARTZ_BLOCK");
            blocks.add("QUARTZ_PILLAR");
            blocks.add("WHITE_CONCRETE");
        }

        return blocks;
    }
}

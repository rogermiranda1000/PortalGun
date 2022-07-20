package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.blocks.ResetBlock;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
    MATERIAL("portalgun.material"),
    CUSTOM_MODEL_DATA("portalgun.custom_model_data"),
    DELETE_ON_DEATH("portals.remove_on_death"),
    REMOVE_ON_LEAVE("portals.remove_on_leave"),
    MAX_LENGHT("portals.placement_length"),
    WHITELIST_BLOCKS("portals.whitelist_blocks"),
    WHITELISTED_BLOCKS("portals.whitelisted_blocks"),
    ONLY_YOUR_PORTALS("portals.use_only_yours"),
    PERSISTANT("portals.save"),
    PARTICLES("portals.particles"),
    CREATE_SOUND("portals.create_sound"),
    TELEPORT_SOUND("portals.teleport_sound"),
    RESTARTER_PARTICLES("restarter.particles");

    private static FileConfiguration fileConfiguration;
    private static HashMap<Config, Object> savedConfiguration;
    private final String key;

    Config(String key) {
        this.key = key;
    }

    public Object getObject() {
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

    public Sound getSound() throws IllegalArgumentException {
        return Sound.valueOf((String) this.getObject());
    }

    public static void loadConfig() {
        Config.loadValidBlocks();

        Config.loadPortalgunMaterial(Config.fileConfiguration.getString(MATERIAL.key), Config.fileConfiguration.contains(CUSTOM_MODEL_DATA.key) ? Config.fileConfiguration.getInt(CUSTOM_MODEL_DATA.key) : null);

        Language.loadHashMap(Config.fileConfiguration.getString(LANGUAGE.key));

        loadPortalParticles();
        loadRestarterParticles();
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

    private static void loadPortalParticles() {
        List<String> particles = Config.fileConfiguration.getStringList(Config.PARTICLES.key);
        if (particles.size() != 2) {
            PortalGun.plugin.printConsoleErrorMessage(Config.PARTICLES.key + " must have only 2 particles!");
            return;
        }

        try {
            Portal.setParticle(VersionController.get().getParticle(particles.get(0)), true);
        } catch (IllegalArgumentException IAEx) {
            PortalGun.plugin.printConsoleErrorMessage("Particle '" + particles.get(0) + "' does not exists.");
        }

        try {
            Portal.setParticle(VersionController.get().getParticle(particles.get(1)), false);
        } catch (IllegalArgumentException IAEx) {
            PortalGun.plugin.printConsoleErrorMessage("Particle '" + particles.get(1) + "' does not exists.");
        }
    }

    private static void loadRestarterParticles() {
        String particle = Config.fileConfiguration.getString(Config.RESTARTER_PARTICLES.key);
        if (particle == null) {
            // < v.2.3
            particle = Config.getDefaultRestarterParticle();
            Config.fileConfiguration.set(Config.RESTARTER_PARTICLES.key, particle);
            PortalGun.plugin.saveConfig();
        }

        try {
            ResetBlock.setParticle(VersionController.get().getParticle(particle));
        } catch (IllegalArgumentException IAEx) {
            PortalGun.plugin.printConsoleErrorMessage("Particle '" + particle + "' does not exists.");
        }
    }

    /**
     * It creates the PortalGun
     * @param material PortalGun's material
     * @param customModelData PortalGun's CustomModelData. NULL or -1 if any
     */
    private static void loadPortalgunMaterial(@Nullable String material, @Nullable Integer customModelData) {
        if (material == null) {
            PortalGun.plugin.printConsoleErrorMessage(MATERIAL.key + " is not setted in config file!");
            return;
        }

        Material portalgunMaterial = Material.getMaterial(material);
        if (portalgunMaterial == null) {
            PortalGun.plugin.printConsoleErrorMessage("PortalGun's item (" + material + ") does not exists.");
            return;
        }

        PortalGun.item = new ItemStack(portalgunMaterial);
        ItemMeta meta = PortalGun.item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "PortalGun");
        if (customModelData != null && customModelData != -1) meta.setCustomModelData(customModelData);
        PortalGun.item.setItemMeta(meta);
        PortalGun.item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    }

    private static void loadValidBlocks() {
        ArrayList<BlockType> allowedBlocks = new ArrayList<>();

        for (String txt : Config.fileConfiguration.getStringList(Config.WHITELISTED_BLOCKS.key)) {
            BlockType o = VersionController.get().getMaterial(txt);
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
        if (VersionController.version.compareTo(Version.MC_1_14) >= 0) c.put(Config.CUSTOM_MODEL_DATA.key, -1);
        c.put(Config.MAX_LENGHT.key, 80);
        c.put(Config.PARTICLES.key, Config.getDefaultParticles());
        c.put(Config.REMOVE_ON_LEAVE.key, true);
        c.put(Config.DELETE_ON_DEATH.key, false);
        c.put(Config.PERSISTANT.key, false);
        c.put(Config.ONLY_YOUR_PORTALS.key, false);
        c.put(Config.WHITELIST_BLOCKS.key, false);
        c.put(Config.WHITELISTED_BLOCKS.key, Config.getDefaultBlocks());
        c.put(Config.TELEPORT_SOUND.key, Config.getDefaultTeleportSound());
        c.put(Config.CREATE_SOUND.key, Config.getDefaultCreateSound());
        c.put(Config.RESTARTER_PARTICLES.key, Config.getDefaultRestarterParticle());

        return c;
    }

    private static String getDefaultTeleportSound() {
        if (VersionController.isPaper) return "ENTITY_SHULKER_TELEPORT"; // TODO check version on paper too

        if (VersionController.version.compareTo(Version.MC_1_9) < 0) return "ENDERMAN_TELEPORT";
        else return "ENTITY_SHULKER_TELEPORT";
    }

    private static String getDefaultCreateSound() {
        if (VersionController.isPaper) return "ENTITY_SLIME_JUMP"; // TODO check version on paper too

        if (VersionController.version.compareTo(Version.MC_1_9) < 0) return "SLIME_WALK2";
        else return "ENTITY_SLIME_JUMP";
    }

    private static String getDefaultRestarterParticle() {
        return (VersionController.version.compareTo(Version.MC_1_13) < 0) ? "CRIT" : "NAUTILUS";
    }

    private static ArrayList<String> getDefaultParticles() {
        ArrayList<String> particles = new ArrayList<>();

        if (VersionController.isPaper) {
            // TODO check version on paper too
            particles.add("FLAME");
            particles.add("VILLAGER_HAPPY");
        }
        else {
            if (VersionController.version.compareTo(Version.MC_1_9) < 0) {
                particles.add("FLAME");
                particles.add("HAPPY_VILLAGER");
            } else {
                particles.add("FLAME");
                particles.add("VILLAGER_HAPPY");
            }
        }

        return particles;
    }

    private static ArrayList<String> getDefaultBlocks() {
        ArrayList<String> blocks = new ArrayList<>();

        if (VersionController.version.compareTo(Version.MC_1_13) < 0) {
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

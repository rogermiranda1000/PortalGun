package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.blocks.ResetBlock;
import com.rogermiranda1000.portalgun.blocks.ResetBlocks;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import com.sun.istack.internal.NotNull;
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

public enum Config {
    LANGUAGE("language"),
    RESOURCEPACK("resourcepack.use"),
    PORTALGUN_NAME("portalgun.name"),
    PORTALGUN_LORE("portalgun.lore"),
    MATERIAL("portalgun.material"),
    CUSTOM_MODEL_DATA("portalgun.custom_model_data"),
    DURABILITY("portalgun.durability"),
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
    RESTARTER_PARTICLES("emancipator.particles"),
    TAKE_ENTITIES("portalgun.take_entities");

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

        PortalGun.useResourcePack = Config.fileConfiguration.getBoolean(RESOURCEPACK.key);
        PortalGun.takeEntities = Config.fileConfiguration.getBoolean(TAKE_ENTITIES.key);

        Config.loadPortalgunMaterial(Config.fileConfiguration.getString(PORTALGUN_NAME.key), Config.fileConfiguration.getStringList(PORTALGUN_LORE.key),
                Config.fileConfiguration.getString(MATERIAL.key), Config.fileConfiguration.contains(CUSTOM_MODEL_DATA.key) ? Config.fileConfiguration.getInt(CUSTOM_MODEL_DATA.key) : null,
                Config.fileConfiguration.contains(DURABILITY.key) ? Config.fileConfiguration.getInt(DURABILITY.key) : null);

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
    private static void loadPortalgunMaterial(@NotNull String name, @NotNull List<String> lore, @NotNull String material, @Nullable Integer customModelData, @Nullable Integer durability) {
        Material portalgunMaterial = Material.getMaterial(material);
        if (portalgunMaterial == null) {
            PortalGun.plugin.printConsoleErrorMessage("PortalGun's item (" + material + ") does not exists.");
            return;
        }

        ItemMeta meta;
        if (durability != null) {
            try {
                PortalGun.item = VersionController.get().setUnbreakable(new ItemStack(portalgunMaterial));
                VersionController.get().setDurability(PortalGun.item, durability);
                //meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            } catch (IllegalArgumentException ex) {
                PortalGun.plugin.printConsoleErrorMessage("Can't use " + PortalGun.item.getType().name() + " with the resourcepack.");
                PortalGun.useResourcePack = false;
                PortalGun.item = new ItemStack(portalgunMaterial);
            } finally {
                meta = PortalGun.item.getItemMeta();
                meta.setDisplayName(name);
                meta.setLore(lore);
                PortalGun.item.setItemMeta(meta);
            }
            return;
        }

        PortalGun.item = new ItemStack(portalgunMaterial);
        meta = PortalGun.item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        // resourcepack & item identificator
        if (customModelData != null) meta.setCustomModelData(customModelData);
        else {
            meta.addEnchant(Enchantment.DURABILITY, 10, true); // we need an identifier
            if (PortalGun.useResourcePack) PortalGun.plugin.printConsoleErrorMessage("The resourcepack won't work on 1.8!");
        }
        PortalGun.item.setItemMeta(meta);
    }

    private static void loadValidBlocks() {
        ArrayList<BlockType> allowedBlocks = new ArrayList<>();

        for (String txt : Config.fileConfiguration.getStringList(Config.WHITELISTED_BLOCKS.key)) {
            BlockType o = VersionController.get().getMaterial(txt);
            if (o != null) allowedBlocks.add(o);
        }

        // TODO: lava restriction?
        // TODO: isPassable?
        Portal.isEmptyBlock = b -> VersionController.get().isPassable(b) && !ResetBlocks.getInstance().insideResetBlock(b.getLocation());
        Portal.isValidBlock = b -> !VersionController.get().isPassable(b) && (!Config.fileConfiguration.getBoolean(Config.WHITELIST_BLOCKS.key) || allowedBlocks.contains(VersionController.get().getObject(b)));
    }

    private static HashMap<String,Object> getDefaultConfiguration() {
        HashMap<String,Object> c = new HashMap<>();

        c.put(Config.LANGUAGE.key, "english");
        if (VersionController.version.compareTo(Version.MC_1_9) >= 0) c.put(Config.RESOURCEPACK.key, true);
        c.put(Config.MATERIAL.key, "IRON_HOE");
        c.put(Config.PORTALGUN_NAME.key, "§6§lPortalGun");
        c.put(Config.PORTALGUN_LORE.key, new String[]{"With the PortalGun you can open portals."});
        if (VersionController.version.compareTo(Version.MC_1_14) >= 0) c.put(Config.CUSTOM_MODEL_DATA.key, 1);
        else if (VersionController.version.compareTo(Version.MC_1_9) >= 0) c.put(Config.DURABILITY.key, 1);
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
        c.put(Config.TAKE_ENTITIES.key, true);

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

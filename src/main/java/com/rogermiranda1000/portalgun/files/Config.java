package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.blocks.ResetBlock;
import com.rogermiranda1000.portalgun.blocks.ResetBlocks;
import com.rogermiranda1000.portalgun.blocks.beam.Beam;
import com.rogermiranda1000.portalgun.cubes.CompanionCube;
import com.rogermiranda1000.portalgun.cubes.RedirectionCube;
import com.rogermiranda1000.portalgun.events.onPortalgunEntity;
import com.rogermiranda1000.portalgun.events.onUse;
import com.rogermiranda1000.portalgun.items.*;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public enum Config {
    LANGUAGE("language"),
    CLEAR_PREFIX("prefix.clear"),
    ERROR_PREFIX("prefix.error"),
    RESOURCEPACK("resourcepack.use"),
    PORTALGUN_NAME("portalgun.name"),
    PORTALGUN_LORE("portalgun.lore"),
    MATERIAL("portalgun.material"),
    CUSTOM_MODEL_DATA("portalgun.custom_model_data"),
    DURABILITY("portalgun.durability"),
    CAST_BEAM("portalgun.cast_beam"),
    SWIPE_COLORS("portalgun.swipe_colors_animation"),
    DELETE_ON_DEATH("portals.remove_on_death"),
    REMOVE_ON_LEAVE("portals.remove_on_leave"),
    MAX_LENGTH("portals.placement_length"),
    WHITELIST_BLOCKS("portals.whitelist_blocks"),
    WHITELISTED_BLOCKS("portals.whitelisted_blocks"),
    ONLY_YOUR_PORTALS("portals.use_only_yours"),
    PERSISTENT("portals.save"),
    PARTICLES("portals.particles"),
    CREATE_SOUND("portals.create_sound"),
    TELEPORT_SOUND("portals.teleport_sound"),
    RESTARTER_PARTICLES("emancipator.particles"),
    TAKE_ENTITIES("portalgun.take_entities.enabled"),
    TAKE_ENTITIES_BLACKLIST("portalgun.take_entities.blacklist"),
    BLACKLISTED_WORLDS("portals.blacklisted_worlds"),
    WG_REGIONS("portals.only_allowed_worldguard_regions"),
    BLACKLIST_WG_REGIONS("portals.denied_worldguard_regions"),
    TELEPORT_ENTITIES_BLACKLIST("portalgun.teleport_entities.blacklist"),
    BEAM_MAX_LENGTH("beam.max_length"),
    BEAM_PARTICLE("beam.particle");

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
        return ((Number)this.getObject()).intValue();
    }

    private float getFloat() {
        return ((Number)this.getObject()).floatValue();
    }

    public Sound getSound() throws IllegalArgumentException {
        return Sound.valueOf((String) this.getObject());
    }

    public static void loadConfig() throws ConfigFileException {
        try {
            Config.loadValidBlocks();

            PortalGun.clearPrefix = Config.fileConfiguration.getString(CLEAR_PREFIX.key);
            PortalGun.errorPrefix = Config.fileConfiguration.getString(ERROR_PREFIX.key);

            PortalGun.useResourcePack = Config.fileConfiguration.getBoolean(RESOURCEPACK.key);
            PortalGun.takeEntities = Config.fileConfiguration.getBoolean(TAKE_ENTITIES.key);
            PortalGun.castBeam = Config.fileConfiguration.getBoolean(CAST_BEAM.key);
            PortalGuns.swipeColorsAnimation = Config.fileConfiguration.getBoolean(SWIPE_COLORS.key);
            PortalGun.blacklistedWorlds = Config.fileConfiguration.getStringList(BLACKLISTED_WORLDS.key);
            PortalGun.wgRegions = (Config.fileConfiguration.getStringList(WG_REGIONS.key).isEmpty() ? null : Config.fileConfiguration.getStringList(WG_REGIONS.key));
            PortalGun.blacklistedWgRegions = (Config.fileConfiguration.getStringList(BLACKLIST_WG_REGIONS.key).isEmpty() ? null : Config.fileConfiguration.getStringList(BLACKLIST_WG_REGIONS.key));

            Beam.MAX_DISTANCE = Config.BEAM_MAX_LENGTH.getFloat();

            String material = Config.fileConfiguration.getString(MATERIAL.key);
            Material portalgunMaterial = Material.getMaterial(material);
            if (portalgunMaterial == null) {
                PortalGun.plugin.printConsoleErrorMessage("PortalGun's item (" + material + ") does not exists.");
                throw new IllegalArgumentException("PortalGun's item (" + material + ") does not exists.");
            }

            String portalgunName = Config.fileConfiguration.getString(PORTALGUN_NAME.key);
            List<String> portalgunLore = Config.fileConfiguration.getStringList(PORTALGUN_LORE.key);
            Config.loadPortalgunMaterial(portalgunName, portalgunLore, portalgunMaterial,
                    Config.fileConfiguration.contains(CUSTOM_MODEL_DATA.key) ? Config.fileConfiguration.getInt(CUSTOM_MODEL_DATA.key) : null,
                    Config.fileConfiguration.contains(DURABILITY.key) ? Config.fileConfiguration.getInt(DURABILITY.key) : null);
            if (PortalGun.useResourcePack && PortalGuns.portalGun instanceof ResourcepackedItem) {
                ResourcepackedItem portalgun = (ResourcepackedItem) PortalGuns.portalGun;

                PortalGuns.orangePortalGun = ResourcepackedItemFactory.createItem(portalgunMaterial, portalgunName, portalgun.getIdentifier()+1, portalgunLore);
                PortalGuns.bluePortalGun = ResourcepackedItemFactory.createItem(portalgunMaterial, portalgunName, portalgun.getIdentifier()+2, portalgunLore);

                CompanionCube.TEXTURE = ResourcepackedItemFactory.createItem(portalgunMaterial, "Weighted Cube", portalgun.getIdentifier()+6);
                RedirectionCube.TEXTURE = ResourcepackedItemFactory.createItem(portalgunMaterial, "Redirection Cube", portalgun.getIdentifier()+8);
            }

            Language.loadHashMap(Config.fileConfiguration.getString(LANGUAGE.key));

            onUse.MAX_LENGTH = Config.MAX_LENGTH.getInteger();
            onUse.CREATE_SOUND = Config.CREATE_SOUND.getSound();

            loadPortalParticles();
            loadRestarterParticles();
            loadPickEntityBlacklist();
            loadTeleportEntityBlacklist();
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new ConfigFileException(ex);
        }
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

    private static void loadPickEntityBlacklist() {
        for (String name : Config.fileConfiguration.getStringList(Config.TAKE_ENTITIES_BLACKLIST.key)) {
            onPortalgunEntity.entityPickBlacklist.add(name.toLowerCase());
        }
    }

    private static void loadTeleportEntityBlacklist() {
        for (String name : Config.fileConfiguration.getStringList(Config.TELEPORT_ENTITIES_BLACKLIST.key)) {
            PortalGun.entityTeleportBlacklist.add(name.toLowerCase());
        }
    }

    private static void loadPortalParticles() throws IllegalArgumentException {
        List<String> particles = Config.fileConfiguration.getStringList(Config.PARTICLES.key);
        if (particles.size() != 2) {
            throw new IllegalArgumentException(Config.PARTICLES.key + " must have only 2 particles!");
        }

        try {
            Portal.setParticle(VersionController.get().getParticle(particles.get(0)), true);
        } catch (IllegalArgumentException IAEx) {
            throw new IllegalArgumentException("Particle '" + particles.get(0) + "' does not exists.");
        }

        try {
            Portal.setParticle(VersionController.get().getParticle(particles.get(1)), false);
        } catch (IllegalArgumentException IAEx) {
            throw new IllegalArgumentException("Particle '" + particles.get(1) + "' does not exists.");
        }

        String laserParticle = Config.fileConfiguration.getString(Config.BEAM_PARTICLE.key);
        try {
            Beam.LASER_PARTICLE = VersionController.get().getParticle(laserParticle);
        } catch (IllegalArgumentException IAEx) {
            throw new IllegalArgumentException("Particle '" + laserParticle + "' does not exists.");
        }
    }

    private static void loadRestarterParticles() throws IllegalArgumentException {
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
            throw new IllegalArgumentException("Particle '" + particle + "' does not exists.");
        }
    }

    /**
     * It creates the PortalGun
     * @param portalgunMaterial PortalGun's material
     * @param customModelData PortalGun's CustomModelData. NULL or -1 if any
     */
    private static void loadPortalgunMaterial(@NotNull String name, @NotNull List<String> lore, @NotNull Material portalgunMaterial, @Nullable Integer customModelData, @Nullable Integer durability) {
        if (durability != null) {
            try {
                PortalGuns.portalGun = new ResourcepackedDamagedItem(portalgunMaterial, name, durability, lore);
            } catch (IllegalArgumentException ex) {
                PortalGun.plugin.printConsoleErrorMessage("Can't use " + PortalGuns.portalGun.getType().name() + " with the resourcepack.");
                PortalGun.useResourcePack = false;
                PortalGuns.portalGun = new ItemStack(portalgunMaterial);

                ItemMeta meta = PortalGuns.portalGun.getItemMeta();
                meta.setDisplayName(name);
                meta.setLore(lore);
                PortalGuns.portalGun.setItemMeta(meta);
            }
        }
        else {
            // resourcepack & item identificator
            if (customModelData != null) {
                if (VersionController.version.compareTo(Version.MC_1_14) < 0) throw new IllegalArgumentException("Using custom model data prior to 1.14");
                PortalGuns.portalGun = new ResourcepackedCMDItem(portalgunMaterial, name, customModelData, lore);
            } else {
                PortalGuns.portalGun = new ItemStack(portalgunMaterial);
                ItemMeta meta = PortalGuns.portalGun.getItemMeta();

                meta.setDisplayName(name);
                meta.setLore(lore);

                meta.addEnchant(Enchantment.DURABILITY, 10, true); // we need an identifier
                if (PortalGun.useResourcePack) PortalGun.plugin.printConsoleErrorMessage("The resourcepack won't work on 1.8!");

                PortalGuns.portalGun.setItemMeta(meta);
            }
        }
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
        c.put(Config.CLEAR_PREFIX.key, "§6§l[PortalGun] §a");
        c.put(Config.ERROR_PREFIX.key, "§6§l[PortalGun] §c");
        c.put(Config.PORTALGUN_NAME.key, "§6§lPortalGun");
        c.put(Config.PORTALGUN_LORE.key, new String[]{"With the PortalGun you can open portals."});
        if (VersionController.version.compareTo(Version.MC_1_14) >= 0) c.put(Config.CUSTOM_MODEL_DATA.key, 1);
        else if (VersionController.version.compareTo(Version.MC_1_9) >= 0) c.put(Config.DURABILITY.key, 1);
        c.put(Config.MAX_LENGTH.key, 80);
        c.put(Config.BEAM_MAX_LENGTH.key, 45);
        c.put(Config.PARTICLES.key, Config.getDefaultParticles());
        c.put(Config.BEAM_PARTICLE.key, "FLAME");
        // TODO config laser hurt player
        c.put(Config.REMOVE_ON_LEAVE.key, true);
        c.put(Config.DELETE_ON_DEATH.key, false);
        c.put(Config.PERSISTENT.key, false);
        c.put(Config.ONLY_YOUR_PORTALS.key, false);
        c.put(Config.WHITELIST_BLOCKS.key, false);
        c.put(Config.WHITELISTED_BLOCKS.key, Config.getDefaultBlocks());
        c.put(Config.TELEPORT_SOUND.key, Config.getDefaultTeleportSound());
        c.put(Config.CREATE_SOUND.key, Config.getDefaultCreateSound());
        c.put(Config.RESTARTER_PARTICLES.key, Config.getDefaultRestarterParticle());
        c.put(Config.TAKE_ENTITIES.key, true);
        c.put(Config.CAST_BEAM.key, true);
        c.put(Config.SWIPE_COLORS.key, true);
        c.put(Config.TAKE_ENTITIES_BLACKLIST.key, getDefaultPickEntitiesBlacklist());
        c.put(Config.BLACKLISTED_WORLDS.key, new String[]{"my-safe-world"});
        c.put(Config.WG_REGIONS.key, new String[]{});
        c.put(Config.BLACKLIST_WG_REGIONS.key, new String[]{});
        c.put(Config.TELEPORT_ENTITIES_BLACKLIST.key, getDefaultTeleportEntitiesBlacklist());

        return c;
    }

    private static Collection<String> getDefaultPickEntitiesBlacklist() {
        Collection<String> r = new ArrayList<>();

        r.add(Player.class.getSimpleName());
        r.add(ExperienceOrb.class.getSimpleName());
        r.add(Item.class.getSimpleName());
        r.add(ItemFrame.class.getSimpleName());
        r.add(EnderCrystal.class.getSimpleName());
        r.add(EnderDragon.class.getSimpleName());
        r.add(Wither.class.getSimpleName());

        if (VersionController.version.compareTo(Version.MC_1_19) >= 0) r.add("Warden");

        return r;
    }

    private static Collection<String> getDefaultTeleportEntitiesBlacklist() {
        Collection<String> r = new ArrayList<>();

        r.add(ItemFrame.class.getSimpleName());
        r.add(EnderCrystal.class.getSimpleName());
        r.add(EnderDragon.class.getSimpleName());
        r.add(Wither.class.getSimpleName());


        if (VersionController.version.compareTo(Version.MC_1_19) >= 0) r.add("Warden");

        return r;
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

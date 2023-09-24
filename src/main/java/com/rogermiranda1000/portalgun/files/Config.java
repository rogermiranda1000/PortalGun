package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.helper.configlib.*;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.blocks.ResetBlock;
import com.rogermiranda1000.portalgun.blocks.ResetBlocks;
import com.rogermiranda1000.portalgun.blocks.ThermalBeam;
import com.rogermiranda1000.portalgun.blocks.ThermalReceiver;
import com.rogermiranda1000.portalgun.blocks.beam.Beam;
import com.rogermiranda1000.portalgun.blocks.decorators.DecoratorFactory;
import com.rogermiranda1000.portalgun.blocks.decorators.PoweredThermalReceiverDecorator;
import com.rogermiranda1000.portalgun.blocks.decorators.ThermalBeamDecorator;
import com.rogermiranda1000.portalgun.blocks.decorators.ThermalReceiverDecorator;
import com.rogermiranda1000.portalgun.cubes.CompanionCube;
import com.rogermiranda1000.portalgun.cubes.RedirectionCube;
import com.rogermiranda1000.portalgun.events.onPlayerJoin;
import com.rogermiranda1000.portalgun.events.onPortalgunEntity;
import com.rogermiranda1000.portalgun.events.onUse;
import com.rogermiranda1000.portalgun.items.*;
import com.rogermiranda1000.portalgun.portals.Portal;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@Configuration
public class Config {
    @Ignore
    private static Config instance = null;

    @Comment({"Language to be used.", "You can find all the languages in the 'languages' folder,", "you can also add new ones."})
    public String language = "english";

    @Configuration
    public static class _Portals {
        @Comment("Should the portals be persistent between server restarts?")
        public boolean save = false;

        @Comment("Should users only be allowed to use their own portals?")
        public boolean useOnlyYours = false;

        @Comment("Remove users' portals when they die.")
        public boolean removeOnDeath = false;

        @Comment("Remove users' portals when they leave the server.")
        public boolean removeOnLeave = true;

        @Comment("Sound to be used when placing a portal.")
        public Sound createSound = Sound.valueOf(Config.getDefaultCreateSound());

        @Comment("Sound to be used when using a portal.")
        public Sound teleportSound = Sound.valueOf(Config.getDefaultTeleportSound());
        @Comment("Max distance where a player can place a portal.")
        public float placementLength = 80;

        @Comment("Portal particles.")
        public List<String> particles = Config.getDefaultParticles();

        @Comment("Use the 'whitelistedBlocks' list.")
        public boolean whitelistBlocks = false;
        @Comment("List of blocks where the user will be able to place portals.")
        public List<String> whitelistedBlocks = Config.getDefaultBlocks();

        @Comment("Worlds where portals aren't allowed.")
        public List<String> blacklistedWorlds = Arrays.asList("my-safe-world");

        @Comment({"WorldGuard regions where portals can be opened.", "Warning: this will prevent opening portals in every other place except this WG regions."})
        public List<String> onlyAllowedWorldguardRegions = new ArrayList<>();

        @Comment("WorldGuard regions where portals aren't allowed.")
        public List<String> deniedWorldguardRegions = new ArrayList<>();
    }
    @Comment("")
    public _Portals portals = new _Portals();

    @Configuration
    public static class _Beam {
        @Comment({"Max distance where a beam is propagated.", "Warning: large numbers may cause lag."})
        public float maxLength = 45;

        @Comment("Particle to be used for the Thermal Discouragement Beam")
        public String particle = "FLAME";
    }
    @Comment("")
    public _Beam beam = new _Beam();

    @Configuration
    public static class _Prefix {
        @Comment("Prefix to be added on every PortalGun message")
        public String clear = "§6§l[PortalGun] §a";

        @Comment("Prefix to be added on every PortalGun error message")
        public String error = "§6§l[PortalGun] §c";
    }
    @Comment("")
    public _Prefix prefix = new _Prefix();

    @Configuration
    public static class _Emancipator {
        public String particles = Config.getDefaultRestarterParticle();
    }
    @Comment("")
    public _Emancipator emancipator = new _Emancipator();

    @Configuration
    public static class _PortalGun {
        @Comment("PortalGun's material name")
        public String name = "§6§lPortalGun";

        @Comment("PortalGun's material info")
        public List<String> lore = Arrays.asList("With the PortalGun you can open portals.");

        @Comment("Material to be used for the PortalGun")
        public Material material = Material.getMaterial("IRON_HOE");

        @Comment("Show a particle trail where the portal is being launched.")
        public boolean castBeam = true;

        @Comment({"Change the PortalGun colors depending on the last pressed key.", "Note: you need to have the 'resourcepack.use' option enabled."})
        public boolean swipeColorsAnimation = true;

        @Configuration
        public static class _TakeEntities {
            @Comment("Allows the users to take entities.")
            public boolean enabled = true;

            @Comment("Denied entities to be grabbed.")
            public List<String> blacklist = Config.getDefaultPickEntitiesBlacklist();
        }
        @Comment("All the configuration related to right-clicking entities.")
        public _TakeEntities takeEntities = new _TakeEntities();
    }
    @Comment("")
    public _PortalGun portalgun = new _PortalGun();

    @Configuration
    public static class _TeleportEntities {
        @Comment("Denied entities to be teleported.")
        public List<String> blacklist = Config.getDefaultTeleportEntitiesBlacklist();
    }
    @Comment("")
    public _TeleportEntities teleportEntities = new _TeleportEntities();

    @Polymorphic
    @Configuration
    public static abstract class _Resourcepack {
        @Comment("Enable the PortalGun resourcepack.")
        public boolean use = (VersionController.version.compareTo(Version.MC_1_9) >= 0);

        @Comment({"Download resourcepack endpoint.", "Set 'PROVIDED' if you force the users to install a custom PortalGun resourcepack via server config."})
        public String endpoint = "http://rogermiranda1000.com/portalgun-v2/index.php";
    }
    public static final class _CMDResourcepack extends _Resourcepack {
        @Comment("Base CustomModelData used for the 3d models.")
        public int customModelData = 1;
    }
    public static final class _DurabilityResourcepack extends _Resourcepack {
        @Comment("Base Durability used for the 3d models.")
        public int durability = 1;
    }
    @Comment({"", "Resourcepack-related configuration.", "DO NOT edit the 'type' field."})
    public _Resourcepack resourcepack = (VersionController.version.compareTo(Version.MC_1_14) >= 0) ? new _CMDResourcepack() : new _DurabilityResourcepack();

    /**
     * @pre call `createAndLoad`
     */
    public static void loadConfig() throws ConfigFileException {
        try {
            Config.loadValidBlocks();

            PortalGun.clearPrefix = Config.getInstance().prefix.clear;
            PortalGun.errorPrefix = Config.getInstance().prefix.error;

            PortalGun.useResourcePack = Config.getInstance().resourcepack.use;
            onPlayerJoin.RESOURCEPACK_BASE_URL = Config.getInstance().resourcepack.endpoint;
            PortalGun.takeEntities = Config.getInstance().portalgun.takeEntities.enabled;
            PortalGun.castBeam = Config.getInstance().portalgun.castBeam;
            PortalGuns.swipeColorsAnimation = Config.getInstance().portalgun.swipeColorsAnimation;
            PortalGun.blacklistedWorlds = Config.getInstance().portals.blacklistedWorlds;
            PortalGun.wgRegions = (Config.getInstance().portals.onlyAllowedWorldguardRegions.isEmpty() ? null : Config.getInstance().portals.onlyAllowedWorldguardRegions);
            PortalGun.blacklistedWgRegions = (Config.getInstance().portals.deniedWorldguardRegions.isEmpty() ? null : Config.getInstance().portals.deniedWorldguardRegions);

            Beam.MAX_DISTANCE = Config.getInstance().beam.maxLength;

            Material portalgunMaterial = Config.getInstance().portalgun.material;
            String portalgunName = Config.getInstance().portalgun.name;
            List<String> portalgunLore = Config.getInstance().portalgun.lore;
            Config.loadPortalgunMaterial(portalgunName, portalgunLore, portalgunMaterial,
                    Config.getInstance().resourcepack instanceof _CMDResourcepack ? ((_CMDResourcepack)Config.getInstance().resourcepack).customModelData : null,
                    Config.getInstance().resourcepack instanceof _DurabilityResourcepack ? ((_DurabilityResourcepack)Config.getInstance().resourcepack).durability : null);
            if (PortalGun.useResourcePack && PortalGuns.portalGun instanceof ResourcepackedItem) {
                ResourcepackedItem portalgun = (ResourcepackedItem) PortalGuns.portalGun;

                PortalGuns.orangePortalGun = ResourcepackedItemFactory.createItem(portalgunMaterial, portalgunName, portalgun.getIdentifier()+1, portalgunLore);
                PortalGuns.bluePortalGun = ResourcepackedItemFactory.createItem(portalgunMaterial, portalgunName, portalgun.getIdentifier()+2, portalgunLore);

                CompanionCube.TEXTURE = ResourcepackedItemFactory.createItem(portalgunMaterial, "Weighted Cube", portalgun.getIdentifier()+6);
                RedirectionCube.TEXTURE = ResourcepackedItemFactory.createItem(portalgunMaterial, "Redirection Cube", portalgun.getIdentifier()+8);

                ThermalReceiverDecorator.THERMAL_RECEIVER = ResourcepackedItemFactory.createItem(portalgunMaterial, "Thermal Receiver", portalgun.getIdentifier()+3);
                PoweredThermalReceiverDecorator.ACTIVE_THERMAL_RECEIVER = ResourcepackedItemFactory.createItem(portalgunMaterial, "Thermal Receiver", portalgun.getIdentifier()+4);
                ThermalReceiver.decoratorFactory = new DecoratorFactory<>(ThermalReceiverDecorator.class);
                ThermalReceiver.poweredDecoratorFactory = new DecoratorFactory<>(PoweredThermalReceiverDecorator.class);

                ThermalBeamDecorator.THERMAL_BEAM = ResourcepackedItemFactory.createItem(portalgunMaterial, "Thermal Beam", portalgun.getIdentifier()+5);
                ThermalBeam.decoratorFactory = new DecoratorFactory<>(ThermalBeamDecorator.class);
            }

            Language.loadHashMap(Config.getInstance().language);

            onUse.MAX_LENGTH = (int) Config.getInstance().portals.placementLength;
            onUse.CREATE_SOUND = Config.getInstance().portals.createSound;

            loadPortalParticles();
            loadRestarterParticles();
            loadPickEntityBlacklist();
            loadTeleportEntityBlacklist();
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new ConfigFileException(ex);
        }
    }

    public static void createAndLoad() {
        File configFile = new File(FileManager.pluginFolder, "config.yml");
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                                                        .setNameFormatter(NameFormatters.LOWER_UNDERSCORE)
                                                        .build();
        if (!configFile.exists()) {
            PortalGun.plugin.getLogger().info("Configuration file not found, creating a new one...");
            try {
                YamlConfigurations.save(configFile.toPath(), Config.class, new Config(), properties);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        Config.instance = YamlConfigurations.load(configFile.toPath(), Config.class, properties);
    }

    private static void loadPickEntityBlacklist() {
        for (String name : Config.getInstance().portalgun.takeEntities.blacklist) {
            onPortalgunEntity.entityPickBlacklist.add(name.toLowerCase());
        }
    }

    private static void loadTeleportEntityBlacklist() {
        for (String name : Config.getInstance().teleportEntities.blacklist) {
            PortalGun.entityTeleportBlacklist.add(name.toLowerCase());
        }
    }

    private static void loadPortalParticles() throws IllegalArgumentException {
        List<String> portalParticles = Config.getInstance().portals.particles;
        if (portalParticles.size() != 2) {
            throw new IllegalArgumentException("'portals.particles' must have only 2 particles!");
        }

        try {
            Portal.setParticle(VersionController.get().getParticle(portalParticles.get(0)), true);
        } catch (IllegalArgumentException IAEx) {
            throw new IllegalArgumentException("Particle '" + portalParticles.get(0) + "' does not exists.");
        }

        try {
            Portal.setParticle(VersionController.get().getParticle(portalParticles.get(1)), false);
        } catch (IllegalArgumentException IAEx) {
            throw new IllegalArgumentException("Particle '" + portalParticles.get(1) + "' does not exists.");
        }

        String laserParticle = Config.getInstance().beam.particle;
        try {
            Beam.LASER_PARTICLE = VersionController.get().getParticle(laserParticle);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Particle '" + laserParticle + "' does not exists.");
        }
    }

    private static void loadRestarterParticles() throws IllegalArgumentException {
        String particle = Config.getInstance().emancipator.particles;
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

        for (String txt : Config.getInstance().portals.whitelistedBlocks) {
            BlockType o = VersionController.get().getMaterial(txt);
            if (o != null) allowedBlocks.add(o);
        }

        // TODO: lava restriction?
        // TODO: isPassable?
        Portal.isEmptyBlock = b -> VersionController.get().isPassable(b) && !ResetBlocks.getInstance().insideResetBlock(b.getLocation());
        Portal.isValidBlock = b -> !VersionController.get().isPassable(b) && (!Config.getInstance().portals.whitelistBlocks || allowedBlocks.contains(VersionController.get().getObject(b)));
    }

    private static List<String> getDefaultPickEntitiesBlacklist() {
        List<String> r = new ArrayList<>();

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

    private static List<String> getDefaultTeleportEntitiesBlacklist() {
        List<String> r = new ArrayList<>();

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

    public static Config getInstance() {
        return Config.instance;
    }
}

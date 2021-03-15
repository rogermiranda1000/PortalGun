package com.rogermiranda1000.versioncontroller;

import com.rogermiranda1000.versioncontroller.blocks.BlockManager;
import com.rogermiranda1000.versioncontroller.blocks.BlockPost13;
import com.rogermiranda1000.versioncontroller.blocks.BlockPre13;
import com.rogermiranda1000.versioncontroller.items.ItemManager;
import com.rogermiranda1000.versioncontroller.items.ItemPost9;
import com.rogermiranda1000.versioncontroller.items.ItemPre9;
import com.rogermiranda1000.versioncontroller.particles.ParticleManager;
import com.rogermiranda1000.versioncontroller.particles.ParticlePost9;
import com.rogermiranda1000.versioncontroller.particles.ParticlePre9;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

/**
 * Singleton object for cross-version compatibility
 */
public class VersionController extends ItemManager implements BlockManager, ParticleManager {
    private static VersionController versionController = null;
    private static int version = VersionController.getVersion();

    private static final BlockManager blockManager = (VersionController.version<13) ? new BlockPre13() : new BlockPost13();
    private static final ItemManager itemManager = (VersionController.version<9) ? new ItemPre9() : new ItemPost9();
    private static final ParticleManager particleManager = (VersionController.version<9) ? new ParticlePre9() : new ParticlePost9();

    /**
     * Get the current minecraft version
     * @return version (1.XX)
     */
    public static int getVersion() {
        return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    }

    public static VersionController get() {
        if (VersionController.versionController == null) VersionController.versionController = new VersionController();
        return VersionController.versionController;
    }

    public @Nullable Object getMaterial(String type) {
        return VersionController.blockManager.getMaterial(type);
    }

    public Object getObject(@NotNull Block block) {
        return VersionController.blockManager.getObject(block);
    }

    public boolean isPassable(@NotNull Block block) {
        return VersionController.blockManager.isPassable(block);
    }

    @Override
    public ItemStack[] getItemInHand(PlayerInventory playerInventory) {
        return VersionController.itemManager.getItemInHand(playerInventory);
    }

    @Override
    public void playParticle(World world, Object particle, Location loc) {
        VersionController.particleManager.playParticle(world, particle, loc);
    }

    @Override
    public void playParticle(Player ply, Object particle, Location loc) {
        VersionController.particleManager.playParticle(ply, particle, loc);
    }

    @Override
    public Object getParticle(String particle) throws IllegalArgumentException {
        return VersionController.particleManager.getParticle(particle);
    }
}

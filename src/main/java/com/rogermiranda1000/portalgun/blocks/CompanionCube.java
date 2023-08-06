package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompanionCube {
    private static final float ARMORSTAND_VERTICAL_OFFSET = -1.3f;
    private final Location originalLocation;
    @Nullable private ArmorStand companionCube;
    /**
     * To make the computations easier, `companionCubeSkeleton` would be the one moved, while only showing `companionCube`.
     */
    @Nullable private ArmorStand companionCubeSkeleton;

    public CompanionCube(@NotNull Location spawnAt) {
        this.originalLocation = spawnAt;
    }

    public boolean isCompanionCube(@NotNull Entity e) {
        return e.equals(companionCube) || e.equals(companionCubeSkeleton);
    }

    public boolean isCompanionCubeSkeleton(@NotNull Entity e) {
        return e.equals(companionCubeSkeleton);
    }

    public ArmorStand getCompanionCubeSkeleton() {
        return this.companionCubeSkeleton;
    }

    public CompanionCube spawn() {
        this.companionCube = (ArmorStand)this.originalLocation.getWorld().spawnEntity(this.originalLocation, EntityType.ARMOR_STAND);
        this.companionCubeSkeleton = (ArmorStand)this.originalLocation.getWorld().spawnEntity(this.originalLocation, EntityType.ARMOR_STAND);

        if (VersionController.version.compareTo(Version.MC_1_10) >= 0) this.companionCube.setGravity(false);
        this.companionCube.setVisible(false);
        this.companionCube.setHelmet(new ItemStack(Material.IRON_BLOCK)); // TODO custom texture

        this.companionCubeSkeleton.setVisible(false);

        return this;
    }

    public void remove() {
        if (this.companionCube == null) return;

        // TODO check if isValid?
        this.companionCube.remove();
        this.companionCubeSkeleton.remove();

        this.companionCube = this.companionCubeSkeleton = null;
    }

    public void tick() {
        if (this.companionCube == null) return;
        this.companionCube.teleport(this.companionCubeSkeleton.getLocation().add(0,ARMORSTAND_VERTICAL_OFFSET,0));
    }
}

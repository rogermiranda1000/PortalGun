package com.rogermiranda1000.portalgun.cubes;

import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.entities.EntityWrapper;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Cube {
    public static final float ARMORSTAND_VERTICAL_OFFSET = -1.3f;
    private final Location originalLocation;
    @Nullable
    private EntityWrapper cube;
    /**
     * To make the computations easier, `companionCubeSkeleton` would be the one moved, while only showing `companionCube`.
     */
    @Nullable private ArmorStand cubeSkeleton;

    public Cube(@NotNull Location spawnAt) {
        this.originalLocation = spawnAt;
    }

    public boolean isCube(@NotNull Entity e) {
        if (this.cube == null) return false;
        return e.equals(this.cube.getEntity()) || e.equals(this.cubeSkeleton);
    }

    public boolean isSkeleton(@NotNull Entity e) {
        return e.equals(cubeSkeleton);
    }

    public ArmorStand getSkeleton() {
        return this.cubeSkeleton;
    }

    public Location getCubeCenter() {
        return this.cubeSkeleton.getLocation().clone().add(0, 0.4f, 0);
    }

    Cube spawn() {
        ArmorStand cube = (ArmorStand)this.originalLocation.getWorld().spawnEntity(this.originalLocation, EntityType.ARMOR_STAND);
        this.cubeSkeleton = (ArmorStand)this.originalLocation.getWorld().spawnEntity(this.originalLocation, EntityType.ARMOR_STAND);

        this.cube = new EntityWrapper(cube);
        this.cube.disableGravity();
        cube.setVisible(false);
        this.applyTexture(cube);

        this.cubeSkeleton.setVisible(false);

        return this;
    }

    protected abstract void applyTexture(ArmorStand stand);

    public void remove() {
        if (this.cube == null) return;

        // TODO check if isValid?
        this.cube.getEntity().remove();
        this.cubeSkeleton.remove();

        this.cube = null;
        this.cubeSkeleton = null;
    }

    public void tick() {
        if (this.cube == null) return;
        this.cube.setLocation(this.cubeSkeleton.getLocation().add(0,ARMORSTAND_VERTICAL_OFFSET,0));
    }

    public Location getSpawnLocation() {
        return this.originalLocation;
    }
}

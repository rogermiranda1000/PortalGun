package com.rogermiranda1000.portalgun.blocks.decorators;

import com.rogermiranda1000.versioncontroller.entities.EntityWrapper;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class ArmorStandDecorator implements Decorator {
    private ArmorStand decorate;

    @Override
    public void decorate(Location location, Vector direction) {
        if (this.decorate != null) this.destroy();

        Location spawnAt = this.getSpawnLocation(location, direction);
        this.decorate = (ArmorStand)spawnAt.getWorld().spawnEntity(spawnAt, EntityType.ARMOR_STAND);
        new EntityWrapper(this.decorate).disableGravity();
        this.decorate.setVisible(false);
        this.decorate.setHelmet(this.getHead());
    }

    protected abstract Location getSpawnLocation(Location location, Vector direction);
    protected abstract @NotNull ItemStack getHead();

    @Override
    public boolean isDecorate(@NotNull Entity e) {
        return e.equals(this.decorate);
    }

    @Override
    public void destroy() {
        if (this.decorate == null) return;
        this.decorate.remove();
    }
}

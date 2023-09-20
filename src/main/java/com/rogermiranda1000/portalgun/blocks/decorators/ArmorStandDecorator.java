package com.rogermiranda1000.portalgun.blocks.decorators;

import com.rogermiranda1000.helper.SentryScheduler;
import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.versioncontroller.entities.EntityWrapper;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class ArmorStandDecorator implements Decorator {
    private ArmorStand decorate;
    private BukkitTask fireTask;

    @Override
    public void decorate(Location location, Vector direction) {
        if (this.isDecorated()) this.destroy();

        Location spawnAt = this.getSpawnLocation(location, direction);
        this.decorate = (ArmorStand)spawnAt.getWorld().spawnEntity(spawnAt, EntityType.ARMOR_STAND);
        new EntityWrapper(this.decorate).disableGravity(); // TODO lost context; calling `destroy()` and `decorate()` will increase RAM usage
        this.decorate.setVisible(false);
        this.decorate.setHelmet(this.getHead());

        this.fireTask = new SentryScheduler(PortalGun.plugin).runTaskTimer(PortalGun.plugin, ()->this.decorate.setFireTicks(1000), 0, 1000);
        this.decorate.setMarker(true);
    }

    protected abstract Location getSpawnLocation(Location location, Vector direction);
    protected abstract @NotNull ItemStack getHead();

    @Override
    public boolean isDecorate(@NotNull Entity e) {
        return e.equals(this.decorate);
    }

    @Override
    public boolean isDecorated() {
        return (this.decorate != null);
    }

    @Override
    public void destroy() {
        if (!this.isDecorated()) return;

        this.decorate.remove();
        this.fireTask.cancel();
        this.decorate = null;
    }
}

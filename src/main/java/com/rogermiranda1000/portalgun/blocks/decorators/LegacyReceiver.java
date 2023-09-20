package com.rogermiranda1000.portalgun.blocks.decorators;

import com.rogermiranda1000.portalgun.cubes.Cube;
import com.rogermiranda1000.versioncontroller.entities.EntityWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class LegacyReceiver implements Decorator {
    private ArmorStand decorate;

    @Override
    public void decorate(Location location, Vector direction) {
        if (this.decorate != null) this.destroy();

        Location spawnAt = new Location(
                location.getWorld(),
                location.getBlockX() + 0.5f + direction.getX()/4,
                location.getBlockY() + Cube.ARMORSTAND_VERTICAL_OFFSET + 0.1,
                location.getBlockZ() + 0.5f + direction.getZ()/4
        );
        this.decorate = (ArmorStand)spawnAt.getWorld().spawnEntity(spawnAt, EntityType.ARMOR_STAND);
        new EntityWrapper(this.decorate).disableGravity();
        this.decorate.setVisible(false);
        this.decorate.setHelmet(new ItemStack(Material.GLASS)); // TODO custom texture
    }

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

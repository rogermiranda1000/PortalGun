package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.portalgun.cubes.Cube;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ThermalReceiver {
    private final Location location;
    private final Vector direction;
    private BlockType block;
    private int poweredBy;
    private ArmorStand decorate;

    public ThermalReceiver(Location location, Vector direction) {
        //if (!direction.isNormalized()) throw new IllegalArgumentException("Direction must be unitary");

        this.location = location;
        this.direction = direction;
        this.poweredBy = 0;
        this.block = VersionController.get().getObject(location.getBlock());
    }

    public void decorate() {
        if (this.decorate != null) this.destroy();

        Location spawnAt = new Location(
                this.location.getWorld(),
                this.location.getBlockX() + 0.5f + direction.getX()/4,
                this.location.getBlockY() + Cube.ARMORSTAND_VERTICAL_OFFSET + 0.1,
                this.location.getBlockZ() + 0.5f + direction.getZ()/4
        );
        this.decorate = (ArmorStand)spawnAt.getWorld().spawnEntity(spawnAt, EntityType.ARMOR_STAND);
        if (VersionController.version.compareTo(Version.MC_1_10) >= 0) this.decorate.setGravity(false); // TODO what if <1.9? will it fall?
        this.decorate.setVisible(false);
        this.decorate.setHelmet(new ItemStack(Material.GLASS)); // TODO custom texture
    }

    public boolean isDecorate(@NotNull Entity e) {
        return e.equals(this.decorate);
    }

    public void destroy() {
        if (this.decorate == null) return;
        this.decorate.remove();
    }

    public void power() {
        this.poweredBy++;
        this.location.getBlock().setType(Material.REDSTONE_BLOCK);
    }

    public void unpower() {
        this.poweredBy--;
        if (this.poweredBy <= 0) this.block.setType(this.location.getBlock());
    }

    public void forceUnpower() {
        this.poweredBy = 0;
        this.block.setType(this.location.getBlock());
    }

    public Location getPosition() {
        return this.location;
    }

    public Vector getDirection() {
        return this.direction;
    }
}

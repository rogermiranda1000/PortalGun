package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.portalgun.blocks.decorators.Decorator;
import com.rogermiranda1000.portalgun.blocks.decorators.DecoratorFactory;
import com.rogermiranda1000.portalgun.blocks.decorators.LegacyReceiver;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ThermalReceiver {
    public static DecoratorFactory<?> decoratorFactory = new DecoratorFactory<>(LegacyReceiver.class);

    private final Location location;
    private final Vector direction;
    private BlockType block;
    private int poweredBy;
    private Decorator decorate;

    public ThermalReceiver(Location location, Vector direction) {
        //if (!direction.isNormalized()) throw new IllegalArgumentException("Direction must be unitary");

        this.location = location;
        this.direction = direction;
        this.poweredBy = 0;
        this.block = VersionController.get().getObject(location.getBlock());

        this.decorate = ThermalReceiver.decoratorFactory.getDecorator();
    }

    public void decorate() {
        this.decorate.decorate(this.location, this.direction);
    }

    public boolean isDecorate(@NotNull Entity e) {
        return this.decorate.isDecorate(e);
    }

    public void destroy() {
        this.decorate.destroy();
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

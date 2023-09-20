package com.rogermiranda1000.portalgun.blocks.decorators;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class NoDecorator implements Decorator {
    private boolean decorated;

    public NoDecorator() {
        this.decorated = false;
    }

    @Override
    public void decorate(Location location, Vector direction) {
        this.decorated = true;
    }

    @Override
    public boolean isDecorate(@NotNull Entity e) {
        return false;
    }

    @Override
    public boolean isDecorated() {
        return this.decorated;
    }

    @Override
    public void destroy() {
        this.decorated = false;
    }
}

package com.rogermiranda1000.portalgun.blocks.decorators;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class NoDecorator implements Decorator {
    @Override
    public void decorate(Location location, Vector direction) { }

    @Override
    public boolean isDecorate(@NotNull Entity e) {
        return false;
    }

    @Override
    public void destroy() { }
}

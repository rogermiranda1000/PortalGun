package com.rogermiranda1000.portalgun.blocks.decorators;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Decorator {
    void decorate(Location location, Vector facing);
    boolean isDecorate(@NotNull Entity e);
    void destroy();
}

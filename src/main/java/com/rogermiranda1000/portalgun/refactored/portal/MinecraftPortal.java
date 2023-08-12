package com.rogermiranda1000.portalgun.refactored.portal;

import org.bukkit.Location;

public class MinecraftPortal extends Portal {
    /**
     * Portal-like portal in Minecraft
     *
     * @param position World position
     * @param normal Out vector
     */
    public MinecraftPortal(Location position, Double[] normal) {
        super(new Long[]{}, normal);
    }
}

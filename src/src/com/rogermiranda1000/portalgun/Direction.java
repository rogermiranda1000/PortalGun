package com.rogermiranda1000.portalgun;

import org.bukkit.entity.Entity;

public enum Direction {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    /**
     * @param rotation entity's yaw
     * @return player's vision direction
     */
    public static Direction getDirection(double rotation) {
        Direction dir = Direction.N; // (0.0D <= rotation && rotation < 22.5D) || (337.5D <= rotation && rotation < 360.0D)

        if (rotation < 0.0D) rotation += 360.0D;

        if (22.5D <= rotation && rotation < 67.5D) dir = Direction.NE;
        else if (67.5D <= rotation && rotation < 112.5D) dir = Direction.E;
        else if (112.5D <= rotation && rotation < 157.5D) dir = Direction.SE;
        else if (157.5D <= rotation && rotation < 202.5D) dir = Direction.S;
        else if (202.5D <= rotation && rotation < 247.5D) dir = Direction.SW;
        else if (247.5D <= rotation && rotation < 292.5D) dir = Direction.W;
        else if (292.5D <= rotation && rotation < 337.5D) dir = Direction.NW;

        return dir;
    }
    public static Direction getDirection(Entity e) {
        return Direction.getDirection(e.getLocation().getYaw());
    }
}

package com.rogermiranda1000.portalgun;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

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
    public boolean diagonal() {
        return this == Direction.NE || this == Direction.SE || this == Direction.SW || this == Direction.NW;
    }

    public Direction getOpposite() {
        return Direction.values()[this.ordinal() + (this.ordinal() > 3 ?  -4 : 4)];
    }

    public static boolean aligned(Direction d1, Direction d2) {
        return d1 == d2 || d1.getOpposite() == d2;
    }

    // TODO: diagonals
    public float getValue() {
        float r = 0.f;

        switch (this) {
            case N:
                r = 0.f;
                break;
            case E:
                r = 90.f;
                break;
            case S:
                r = 180.f;
                break;
            case W:
                r = 270.f;
                break;
        }

        return r;
    }

    // TODO: diagonals
    public Location addOneBlock(Location loc) {
        loc.add(0.f, 0.f, this == Direction.N ? 1.f:0.f);
        loc.add(0.f, 0.f, this == Direction.S ? -1.f:0.f);
        loc.add(this == Direction.E ? 1.f:0.f, 0.f, 0.f);
        loc.add(this == Direction.W ? -1.f:0.f, 0.f, 0.f);
        return loc;
    }

    public Vector addingVector() {
        return addOneBlock(new Location(null, 0.f, 0.f, 0.f)).toVector();
    }
}

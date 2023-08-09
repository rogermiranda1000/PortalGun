package com.rogermiranda1000.portalgun.utils;

import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import com.github.davidmoten.rtreemulti.geometry.internal.RectangleDouble;
import org.bukkit.Location;

// TODO migrate to VersionController
public class RTreeConverter {
    public static final double EPSILON = 1e-6;

    public static Point getPoint(Location loc) {
        if (loc.getWorld() == null) return Point.create(0,0,loc.getX(), loc.getY(), loc.getZ());

        return Point.create(Double.longBitsToDouble(loc.getWorld().getUID().getMostSignificantBits()),
                Double.longBitsToDouble(loc.getWorld().getUID().getLeastSignificantBits()),
                loc.getX(), loc.getY(), loc.getZ());
    }

    public static Rectangle getPointWithMargin(Location loc) {
        Point pos = RTreeConverter.getPoint(loc);
        double []vals = pos.values(),
                mins = new double[vals.length],
                maxs = new double[mins.length];
        for (int n = 0; n < mins.length; n++) mins[n] = vals[n] - EPSILON;
        for (int n = 0; n < maxs.length; n++) maxs[n] = vals[n] + EPSILON;
        return RectangleDouble.create(mins, maxs);
    }

    public static Rectangle getRectangle(Location ...points) {
        if (points.length < 2) throw new IllegalArgumentException("You should provide at least 2 arguments");
        double []min = RTreeConverter.getPoint(points[0]).values(),
                max = RTreeConverter.getPoint(points[0]).values();
        for (int n = 1; n < points.length; n++) {
            double []current = RTreeConverter.getPoint(points[n]).values();
            for (int index = 0; index < min.length; index++) min[index] = Math.min(min[index], current[index]);
            for (int index = 0; index < max.length; index++) max[index] = Math.max(max[index], current[index]);
        }
        return RectangleDouble.create(min, max);
    }
}

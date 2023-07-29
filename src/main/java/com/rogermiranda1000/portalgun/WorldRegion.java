package com.rogermiranda1000.portalgun;

import com.rogermiranda1000.helper.worldguard.RegionDelimiter;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashSet;

public class WorldRegion implements RegionDelimiter {
    private final Collection<String> blacklistedWorlds;

    public WorldRegion(Collection<String> blacklistedWorlds) {
        this.blacklistedWorlds = new HashSet<>(blacklistedWorlds);
    }

    @Override
    public boolean isInsideRegion(Location location, Collection<String> collection) {
        if (location.getWorld() == null) return true;
        return !this.blacklistedWorlds.contains(location.getWorld().getName());
    }
}

package com.rogermiranda1000.portalgun.api;

import com.rogermiranda1000.portalgun.portals.Portal;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public interface PortalGunAccessibleMethods {
    boolean castPortal(Player p, boolean isLeft);

    ArrayList<Portal> getPortals();

    void removePortal(Portal p);

    void removePortals(Player p);
}

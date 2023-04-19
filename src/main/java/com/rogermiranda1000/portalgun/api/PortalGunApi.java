package com.rogermiranda1000.portalgun.api;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.entity.Player;

public class PortalGunApi implements PortalGunAccessibleMethods {
    private static PortalGunApi portalGunApi = null;

    private PortalGunApi() {}

    /**
     * Get the singleton class
     * @return Object to run all the PortalGun API's functions
     */
    public static PortalGunApi getInstance() {
        if (PortalGunApi.portalGunApi == null) PortalGunApi.portalGunApi = new PortalGunApi();
        return PortalGunApi.portalGunApi;
    }

    @Override
    public boolean castPortal(Player p, boolean isLeft) {
        return PortalGun.plugin.castPortal(p, isLeft);
    }
}

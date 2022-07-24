package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onPlayerJoin implements Listener {
    /**
     * Given a GET argument 'tool' and the pack_format 'format', it generates a resourcepack
     */
    private static final String RESOURCEPACK_BASE_URL = "https://rogermiranda1000.com/PortalGun/index.php";
    private static Material portalgun;

    public static void setPortalGun(Material mat) {
        portalgun = mat;
    }

    private static String getUrl() {
        return RESOURCEPACK_BASE_URL + "?tool=" + portalgun.name() + "&format=" + getPackFormat();
    }

    /**
     * Format	Versions
     * 1        1.6.1–1.8.9
     * 2	    1.9–1.10.2
     * 3	    1.11–1.12.2
     * 4	    1.13–1.14.4
     * 5	    1.15–1.16.1
     * 6	    1.16.2–1.16.5
     * 7	    1.17
     * @return pack_format
     */
    private static short getPackFormat() {
        if (VersionController.version.compareTo(Version.MC_1_9) < 0) return 1;
        else if (VersionController.version.compareTo(Version.MC_1_11) < 0) return 2;
        else if (VersionController.version.compareTo(Version.MC_1_13) < 0) return 3;
        else if (VersionController.version.compareTo(Version.MC_1_15) < 0) return 4;
        else if (VersionController.version.compareTo(Version.MC_1_16_2) < 0) return 5;
        else if (VersionController.version.compareTo(Version.MC_1_17) < 0) return 6;
        else return 7; // Version >= 1.17
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().setResourcePack(getUrl());
    }
}
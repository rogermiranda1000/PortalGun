package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onPlayerJoin implements Listener {
    /**
     * Given a GET argument 'tool', 'damage'/'custom_model_data' and the pack_format 'format', it generates a resourcepack
     */
    private static final String RESOURCEPACK_BASE_URL = "http://rogermiranda1000.com/portalgun/index.php"; // TODO custom URL

    private static String getUrl() {
        String identifierKey = null, identifierValue = "";
        if (VersionController.version.compareTo(Version.MC_1_14) >= 0) {
            identifierKey = "custom_model_data";
            identifierValue = String.valueOf(PortalGun.item.getItemMeta().getCustomModelData());
        }
        else if (VersionController.version.compareTo(Version.MC_1_9) >= 0) {
            identifierKey = "damage";
            identifierValue = String.valueOf(((float)VersionController.get().getDurability(PortalGun.item)) / PortalGun.item.getType().getMaxDurability());
        }

        return RESOURCEPACK_BASE_URL + "?tool=" + PortalGun.item.getType().name() + "&format=" + getPackFormat() +
                ((identifierKey == null) ? "" : ("&" + identifierKey + "=" + identifierValue));
    }

    /**
     * Format	Versions
     * 1        1.6.1–1.8.9
     * 2	    1.9–1.10.2
     * 3	    1.11–1.12.2
     * 4	    1.13–1.14.4
     * 5	    1.15–1.16.1
     * 6	    1.16.2–1.16.5
     * 7	    1.17.x
     * 8        1.18.x
     * 9        1.19.x
     * @return pack_format
     */
    private static short getPackFormat() {
        if (VersionController.version.compareTo(Version.MC_1_9) < 0) return 1;
        else if (VersionController.version.compareTo(Version.MC_1_11) < 0) return 2;
        else if (VersionController.version.compareTo(Version.MC_1_13) < 0) return 3;
        else if (VersionController.version.compareTo(Version.MC_1_15) < 0) return 4;
        else if (VersionController.version.compareTo(Version.MC_1_16_2) < 0) return 5;
        else if (VersionController.version.compareTo(Version.MC_1_17) < 0) return 6;
        else if (VersionController.version.compareTo(Version.MC_1_18) < 0) return 7;
        else if (VersionController.version.compareTo(Version.MC_1_19) < 0) return 8;
        else return 9; // Version >= 1.19
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!PortalGun.useResourcePack) return;
        Bukkit.getScheduler().runTaskLater(PortalGun.plugin, ()->e.getPlayer().setResourcePack(getUrl()), 20L);
    }
}
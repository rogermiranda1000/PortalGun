package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.portalgun.items.PortalGuns;
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
    private static final String RESOURCEPACK_BASE_URL = "http://rogermiranda1000.com/portalgun-v2/index.php"; // TODO add endpoint to config file

    private static String getUrl() {
        String identifierKey = null, identifierValue = "";
        if (VersionController.version.compareTo(Version.MC_1_14) >= 0) {
            identifierKey = "custom_model_data";
            identifierValue = String.valueOf(PortalGuns.portalGun.getItemMeta().getCustomModelData());
        }
        else if (VersionController.version.compareTo(Version.MC_1_9) >= 0) {
            identifierKey = "damage";
            identifierValue = String.valueOf(VersionController.get().getDurability(PortalGuns.portalGun));
        }

        return RESOURCEPACK_BASE_URL + "?tool=" + PortalGuns.portalGun.getType().name() + "&format=" + getPackFormat() +
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
     * 9        1.19-1.19.2
     * 12       1.19.3
     * 13       1.19.4
     * 15       1.20-1.20.1
     * @author <a href="https://minecraft.fandom.com/wiki/Pack_format">Minecraft pack_format</a>
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
        else if (VersionController.version.compareTo(Version.MC_1_19_3) < 0) return 9; // between 1.19 and 1.19.2
        else if (VersionController.version.compareTo(Version.MC_1_19_4) < 0) return 12; // 1.19.3
        else if (VersionController.version.compareTo(Version.MC_1_20) < 0) return 13; // 1.19.4
        else return 15; // Version >= 1.20
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!PortalGun.useResourcePack) return;
        Bukkit.getScheduler().runTaskLater(PortalGun.plugin, ()->e.getPlayer().setResourcePack(getUrl()), 20L);
    }
}
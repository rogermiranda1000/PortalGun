package com.rogermiranda1000.portalgun.versioncontroller;

import org.bukkit.Bukkit;

public class VersionController {
    /**
     * @return minecraft version (1.XX)
     */
    public static int getVersion() {
        return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    }
}

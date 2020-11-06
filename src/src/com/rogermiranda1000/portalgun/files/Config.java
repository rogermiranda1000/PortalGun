package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static FileConfiguration config;

    public static void initConfig() {
        Config.config = PortalGun.plugin.getConfig();
    }
}

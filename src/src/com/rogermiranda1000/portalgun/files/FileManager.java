package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private static File pluginFolder;

    public static void loadFiles() {
        FileManager.pluginFolder = PortalGun.plugin.getDataFolder();
        if (!FileManager.pluginFolder.exists()) FileManager.pluginFolder.mkdir();
        File configFile = new File(FileManager.pluginFolder, "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Language.languagePath = new File(FileManager.pluginFolder + File.separator + "languages");
        Language.checkAndCreate();

        Config.initConfig();

        Language.loadHashMap(Config.config.getString("language"));
    }
}

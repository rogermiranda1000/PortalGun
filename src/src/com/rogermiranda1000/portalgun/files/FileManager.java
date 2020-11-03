package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;

import java.io.File;
import java.io.IOException;

public class FileManager {
    public static void loadFiles() {
        File f = PortalGun.instancia.getDataFolder();
        if (!f.exists()) f.mkdir();
        File configFile = new File(f, "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Language.languagePath = new File(f + File.separator + "languages");
        Language.checkAndCreate();

        Config.initConfig();

        Language.loadHashMap(Config.config.getString("language"));
    }
}

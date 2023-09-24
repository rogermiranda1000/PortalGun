package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;

import java.io.File;

public class FileManager {
    public static File pluginFolder;

    public static void loadFiles() throws ConfigFileException {
        FileManager.pluginFolder = PortalGun.plugin.getDataFolder();
        if (!FileManager.pluginFolder.exists()) FileManager.pluginFolder.mkdir();

        Language.languagePath = new File(FileManager.pluginFolder + File.separator + "languages");
        Language.checkAndCreate();

        Config.createAndLoad();
        Config.loadConfig();
    }
}

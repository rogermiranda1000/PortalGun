package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;

import java.io.File;

public class FileManager {
    public static File pluginFolder;

    public static void loadFiles() {
        FileManager.pluginFolder = PortalGun.plugin.getDataFolder();
        if (!FileManager.pluginFolder.exists()) FileManager.pluginFolder.mkdir();

        Language.languagePath = new File(FileManager.pluginFolder + File.separator + "languages");
        Language.checkAndCreate();

        Config.checkAndCreate();
        Config.loadConfig();


        // TODO: Cargar portales
    /*if (config.getBoolean("keep_portals_on_stop")) {
      getLogger().info("Loading portals...");
      File file = new File(getDataFolder(), "portal.yml");
      if(file.exists()) {
          try {
              BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
              String l;
              while ((l=br.readLine())!=null) {
                  String[] args = l.split(">");
                  if(args.length!=3) continue;
                  portales.put(args[0], new LPortal(args[1].split(","),args[2].split(",")));
              }
              br.close();
          } catch (Exception e) { e.printStackTrace(); }

        cancelPortals(true);
      }
    }*/
    }
}

package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public enum Language {
    HELP_GET("help.get_portalgun"),
    HELP_REMOVE("help.remove_portals"),
    HELP_REMOVE_ALL("help.remove_all_portals"),
    PORTAL_DENIED("portal.deny"),
    PORTAL_BLOCK_DENIED("portal.block_deny"),
    PORTAL_OPENED("portal.open"),
    PORTAL_COLLIDING("portal.collides"),
    USER_NO_PERMISSIONS("user.no_permissions"),
    USER_GET("user.get"),
    USER_NO_PORTALS("user.no_portals"),
    USER_REMOVE("user.remove"),
    USER_DEATH("user.remove_death"),
    USER_REMOVE_ALL("user.remove_all");

    private static HashMap<Language, String> translations;
    public static File languagePath;
    private final String key;

    Language(String key) {
        this.key = key;
    }

    /**
     * @return the input's translation
     */
    public String getText() {
        return Language.translations.get(this);
    }

    /**
     * @param match getText({"match1", "replacement1"}, {"match2", "replacement2"}, ...)
     * @return the input's translation with replacement
     */
    public String getText(String[]... match) {
        String txt = this.getText();

        for (String[] current: match) {
            if (current != null && current.length == 2) txt = txt.replace("[" + current[0] + "]", current[1]);
        }

        return txt;
    }


    public static void loadHashMap(String languageName) {
        // get file
        YamlConfiguration lang = new YamlConfiguration();
        File languageFile = new File(Language.languagePath,languageName + ".yml");
        try {
            if (!languageFile.exists() && !Language.createLanguageFile(languageName)) {
                PortalGun.instancia.getLogger().info("Language file '" + languageName + "' does not exists (and cannot be created). Using english file instead.");
                Language.loadHashMap("english");
            }
            else {
                lang.load(languageFile);

                // load
                Language.translations = new HashMap<Language, String>();
                for(Language l : Language.values()) Language.translations.put(l, lang.getString(l.key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkAndCreate() {
        if (!Language.languagePath.exists()) {
            Language.languagePath.mkdir();
            Language.createLanguageFile("english");
            Language.createLanguageFile("español");
            Language.createLanguageFile("català");
        }
    }

    /**
     * @param language "english", "español", "català"
     * @return true: created; false: no able to create
     */
    // TODO: español & català
    public static boolean createLanguageFile(String language) {
        if (!language.equalsIgnoreCase("english") && !language.equalsIgnoreCase("español") && !language.equalsIgnoreCase("català"))
            return false;

        YamlConfiguration lang = new YamlConfiguration();
        File languageFile = new File(Language.languagePath, language + ".yml");
        if (!languageFile.exists()) {
            try {
                lang.load(languageFile);
                if (language.equalsIgnoreCase("english")) lang.addDefaults(getEnglishFile());
                else if (language.equalsIgnoreCase("español")) lang.addDefaults(getEnglishFile());
                else if (language.equalsIgnoreCase("català")) lang.addDefaults(getEnglishFile());
                lang.save(languageFile);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return true;
    }

    private static HashMap<String, Object> getEnglishFile() {
        HashMap<String, Object> r = new HashMap<String, Object>();

        r.put(Language.PORTAL_DENIED.key, "You can't open a portal here.");
        r.put(Language.PORTAL_BLOCK_DENIED.key, "You can't open a portal in that block.");
        r.put(Language.USER_NO_PERMISSIONS.key, "You don't have permissions to do this.");
        r.put(Language.PORTAL_OPENED.key, "Portal opened at [pos].");
        r.put(Language.USER_NO_PORTALS.key, "You don't have any opened portals right now.");
        r.put(Language.PORTAL_COLLIDING.key, "You can't place both portals at the same block!");
        r.put(Language.USER_REMOVE.key, "You deleted successfully your portals.");
        r.put(Language.USER_DEATH.key, "Your portals have been deleted due to your death.");
        r.put(Language.USER_REMOVE_ALL.key, "Deleted all portals.");
        r.put(Language.USER_GET.key, "PortalGun gived!");
        r.put(Language.HELP_GET.key, "Get your PortalGun.");
        r.put(Language.HELP_REMOVE.key, "Delete your active portals.");
        r.put(Language.HELP_REMOVE_ALL.key, "Delete all the active portals.");

        return r;
    }
}

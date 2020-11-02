package com.rogermiranda1000.portalgun;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public enum Language {
    ERROR("general.error"),
    OK("general.ok");

    private static HashMap<Language, String> translations;
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
        String[] current;

        for (int x = 0; x < match.length; x++)  {
            current = match[x];
            if (current != null && current.length == 2) txt = txt.replace("[" + current[0] + "]", current[1]);
        }

        return txt;
    }

    /**
     * @param config file to load the translations
     */
    public static void loadHashMap(YamlConfiguration config) {
        Language.translations = new HashMap<Language, String>();
        for(Language l : Language.values()) Language.translations.put(l, config.getString(l.key));
    }
}

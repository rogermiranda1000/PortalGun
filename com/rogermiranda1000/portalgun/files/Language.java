package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum Language {
    HELP_GET_GUN("help.get_portalgun"),
    HELP_GET_BOOTS("help.get_portalboots"),
    HELP_GET_RESTARTER("help.get_restarter"),
    HELP_REMOVE("help.remove_portals"),
    HELP_REMOVE_OTHERS("help.remove_others_portals"),
    HELP_REMOVE_ALL("help.remove_all_portals"),
    HELP_UNKNOWN("help.unknown_command"),
    PORTAL_DENIED("portal.deny"),
    PORTAL_OPENED("portal.open"),
    PORTAL_COLLIDING("portal.collides"),
    PORTAL_FAR("portal.far"),
    USER_NO_PERMISSIONS("user.no_permissions"),
    USER_NOT_FOUND("user.not_found"),
    USER_GET_GUN("user.get_portalgun"),
    USER_GET_BOOTS("user.get_portalboots"),
    USER_GET_RESTARTER("user.get_restarter"),
    USER_NO_PORTALS("user.no_portals"),
    OTHER_USER_NO_PORTALS("user.other_no_portals"),
    USER_REMOVE("user.remove"),
    USER_REMOVE_OTHERS("user.remove_others"),
    OTHER_USER_REMOVE("user.other_remove"),
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
                PortalGun.plugin.printConsoleErrorMessage("Language file '" + languageName + "' does not exists (and cannot be created). Using english file instead.");
                Language.loadHashMap("english");
            }
            else {
                lang.load(languageFile);

                // load
                String translation;
                Language.translations = new HashMap<>();
                for(Language l : Language.values()) {
                    translation = lang.getString(l.key);
                    if (translation == null) {
                        PortalGun.plugin.printConsoleWarningMessage(l.key + " not defined in language file, picking the default one...");
                        translation = (String) Language.getEnglishFile().get(l.key);

                        // save existing file
                        lang.set(l.key, translation);
                        lang.save(languageFile);
                    }
                    Language.translations.put(l, translation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkAndCreate() {
        if (!Language.languagePath.exists()) {
            PortalGun.plugin.getLogger().info("Creating language files...");
            Language.languagePath.mkdir();
            Language.createLanguageFile("english");
            Language.createLanguageFile("spanish");
            Language.createLanguageFile("catalan");
        }
    }

    /**
     * @param language "english", "español", "català"
     * @return true: created; false: no able to create
     */
    public static boolean createLanguageFile(String language) {
        if (!language.equalsIgnoreCase("english") && !language.equalsIgnoreCase("spanish") && !language.equalsIgnoreCase("catalan"))
            return false;

        final YamlConfiguration lang = new YamlConfiguration();
        final File languageFile = new File(Language.languagePath, language + ".yml");
        try {
            languageFile.createNewFile();

            if (language.equalsIgnoreCase("english")) Language.addValues(lang, Language.getEnglishFile());
            else if (language.equalsIgnoreCase("spanish")) Language.addValues(lang, Language.getSpanishFile());
            else if (language.equalsIgnoreCase("catalan")) Language.addValues(lang, Language.getCatalanFile());

            lang.save(languageFile);
        } catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

    private static void addValues(YamlConfiguration lang, HashMap<String, Object> hashMap) {
        for (Map.Entry<String, Object> entrada : hashMap.entrySet()) lang.set(entrada.getKey(), entrada.getValue());
    }

    private static HashMap<String, Object> getEnglishFile() {
        HashMap<String, Object> r = new HashMap<>();

        r.put(Language.PORTAL_DENIED.key, "You can't open a portal here.");
        r.put(Language.USER_NO_PERMISSIONS.key, "You don't have permissions to do this!");
        r.put(Language.PORTAL_OPENED.key, "[player] has opened a portal at [pos].");
        r.put(Language.USER_NO_PORTALS.key, "You don't have any opened portals right now.");
        r.put(Language.PORTAL_COLLIDING.key, "You can't place both portals at the same block!");
        r.put(Language.PORTAL_FAR.key, "That block is too far to place a portal.");
        r.put(Language.USER_REMOVE.key, "You have removed successfully your portals.");
        r.put(Language.OTHER_USER_REMOVE.key, "[player] has removed your portals.");
        r.put(Language.USER_REMOVE_OTHERS.key, "You have removed [player]'s portals.");
        r.put(Language.OTHER_USER_NO_PORTALS.key, "[player] doesn't have any opened portals.");
        r.put(Language.USER_DEATH.key, "Your portals have been removed due to your death.");
        r.put(Language.USER_REMOVE_ALL.key, "You have removed all portals.");
        r.put(Language.USER_GET_GUN.key, "PortalGun gived!");
        r.put(Language.USER_GET_BOOTS.key, "PortalBoots gived!");
        r.put(Language.USER_GET_RESTARTER.key, "Restarter block gived! Place one and another over it and you'll create a \"door\" that will reset portals.");
        r.put(Language.HELP_GET_GUN.key, "Get the PortalGun.");
        r.put(Language.HELP_GET_BOOTS.key, "Get the PortalBoots.");
        r.put(Language.HELP_GET_RESTARTER.key, "Get the portals restarter.");
        r.put(Language.HELP_REMOVE.key, "Delete your active portals.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Delete others' active portals.");
        r.put(Language.HELP_REMOVE_ALL.key, "Delete all the active portals.");
        r.put(Language.USER_NOT_FOUND.key, "[player] not found.");
        r.put(Language.HELP_UNKNOWN.key, "Unknown command.");


        return r;
    }

    private static HashMap<String, Object> getSpanishFile() {
        HashMap<String, Object> r = new HashMap<>();

        r.put(Language.PORTAL_DENIED.key, "No puedes abrir un portal aquí.");
        r.put(Language.USER_NO_PERMISSIONS.key, "No tienes permisos para hacer eso.");
        r.put(Language.PORTAL_OPENED.key, "[player] ha abierto un portal en [pos].");
        r.put(Language.USER_NO_PORTALS.key, "No tienes portales abiertos.");
        r.put(Language.PORTAL_COLLIDING.key, "No puedes colocar dos portales en el mismo bloque!");
        r.put(Language.PORTAL_FAR.key, "Ese bloque está demasiado lejor.");
        r.put(Language.USER_REMOVE.key, "Tus portales se han eliminado.");
        r.put(Language.OTHER_USER_REMOVE.key, "[player] ha eliminado tus portales.");
        r.put(Language.USER_DEATH.key, "Has muerto, tus portales han sido eliminados.");
        r.put(Language.USER_REMOVE_ALL.key, "Todos los portales han sido eliminados.");
        r.put(Language.USER_GET_GUN.key, "PortalGun recibida!");
        r.put(Language.USER_GET_BOOTS.key, "PortalBoots recibidas!");
        r.put(Language.USER_GET_RESTARTER.key, "Se ha recibido el reiniciador de portales! Coloca uno y otro encima y crearás una \"puerta\" que reiniciará los portales.");
        r.put(Language.HELP_GET_GUN.key, "Obtén la PortalGun.");
        r.put(Language.HELP_GET_BOOTS.key, "Obtén las PortalBoots.");
        r.put(Language.HELP_GET_RESTARTER.key, "Obtén el reiniciador de portales.");
        r.put(Language.HELP_REMOVE.key, "Elimina tus portales activos.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Elimina los portales activos de otro usuario.");
        r.put(Language.HELP_REMOVE_ALL.key, "Elimina todos los portales.");
        r.put(Language.USER_NOT_FOUND.key, "El usuario [player] no se ha encontrado.");
        r.put(Language.USER_REMOVE_OTHERS.key, "Has eliminado los portales de [player].");
        r.put(Language.OTHER_USER_NO_PORTALS.key, "[player] no tiene portales abiertos.");
        r.put(Language.HELP_UNKNOWN.key, "Comando desconocido.");

        return r;
    }

    private static HashMap<String, Object> getCatalanFile() {
        HashMap<String, Object> r = new HashMap<>();

        r.put(Language.PORTAL_DENIED.key, "No pots obrir un portal aquí.");
        r.put(Language.USER_NO_PERMISSIONS.key, "No tens permisos per fer això.");
        r.put(Language.PORTAL_OPENED.key, "[player] ha obert un portal en [pos].");
        r.put(Language.USER_NO_PORTALS.key, "No tens portals oberts.");
        r.put(Language.PORTAL_COLLIDING.key, "No pots colocar dos portals en el mateix bloc!");
        r.put(Language.PORTAL_FAR.key, "Aquell bloc està molt lluny.");
        r.put(Language.USER_REMOVE.key, "Els teus portals s'han eliminat.");
        r.put(Language.OTHER_USER_REMOVE.key, "[player] ha eliminat els teus portals.");
        r.put(Language.USER_DEATH.key, "Has mort, els teus portals s'han eliminat.");
        r.put(Language.USER_REMOVE_ALL.key, "S'han eliminat tots els portals.");
        r.put(Language.USER_GET_GUN.key, "PortalGun obtinguda!");
        r.put(Language.USER_GET_BOOTS.key, "PortalBoots obtingudes!");
        r.put(Language.USER_GET_RESTARTER.key, "S'ha obtingut el reiniciador de portals! Col·loca un i un altre a sobre i crearàs una \"porta\" que reiniciarà els portals.");
        r.put(Language.HELP_GET_GUN.key, "Adquireix la PortalGun.");
        r.put(Language.HELP_GET_BOOTS.key, "Adquireix les PortalBoots.");
        r.put(Language.HELP_GET_RESTARTER.key, "Adquireix el reiniciador de portals.");
        r.put(Language.HELP_REMOVE.key, "Elimina els teus portals actius.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Elimina els portals actius d'un altre usuari.");
        r.put(Language.HELP_REMOVE_ALL.key, "Elimina tots els portals.");
        r.put(Language.USER_NOT_FOUND.key, "L'usuari [player] no s'ha trobat.");
        r.put(Language.USER_REMOVE_OTHERS.key, "Has eliminat els portals de [player].");
        r.put(Language.OTHER_USER_NO_PORTALS.key, "[player] no té portals oberts.");
        r.put(Language.HELP_UNKNOWN.key, "Comanda desconeguda.");

        return r;
    }
}

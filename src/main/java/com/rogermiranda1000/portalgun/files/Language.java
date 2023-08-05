package com.rogermiranda1000.portalgun.files;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum Language {
    HELP_GET_GUN("help.get_portalgun"),
    HELP_GET_BOOTS("help.get_portalboots"),
    HELP_GET_EMANCIPATOR("help.get_emancipator"),
    HELP_REMOVE("help.remove_portals"),
    HELP_REMOVE_OTHERS("help.remove_others_portals"),
    HELP_REMOVE_ALL("help.remove_all_portals"),
    HELP_COMPANION("help.companion_cube"),
    HELP_REPORT("help.report"),
    ERROR_UNKNOWN("error.unknown_command"),
    ERROR_WORLD("error.unknown_world"),
    PORTAL_DENIED("portal.deny"),
    PORTAL_OPENED("portal.open"),
    PORTAL_COLLIDING("portal.collides"),
    PORTAL_FAR("portal.far"),
    USER_NO_PERMISSIONS("user.no_permissions"),
    USER_NOT_FOUND("user.not_found"),
    USER_GET_GUN("user.get_portalgun"),
    USER_GET_BOOTS("user.get_portalboots"),
    USER_GET_EMANCIPATOR("user.get_emancipator"),
    USER_NO_PORTALS("user.no_portals"),
    OTHER_USER_NO_PORTALS("user.other_no_portals"),
    USER_REMOVE("user.remove"),
    USER_REMOVE_OTHERS("user.remove_others"),
    OTHER_USER_REMOVE("user.other_remove"),
    USER_DEATH("user.remove_death"),
    USER_REMOVE_ALL("user.remove_all"),
    REPORT_SENT("report.sent"),
    REPORT_CONTACT_ERROR("report.contact_error");

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
            Language.createLanguageFile("polish");
            Language.createLanguageFile("czech");
            Language.createLanguageFile("german");
        }
    }

    /**
     * @param language "english", "español", "català"
     * @return true: created; false: no able to create
     */
    public static boolean createLanguageFile(String language) {
        if (!language.equalsIgnoreCase("english") && !language.equalsIgnoreCase("spanish") && !language.equalsIgnoreCase("catalan")
                && !language.equalsIgnoreCase("polish") && !language.equalsIgnoreCase("czech") && !language.equalsIgnoreCase("german"))
            return false;

        final YamlConfiguration lang = new YamlConfiguration();
        final File languageFile = new File(Language.languagePath, language + ".yml");
        try {
            languageFile.createNewFile();

            if (language.equalsIgnoreCase("english")) Language.addValues(lang, Language.getEnglishFile());
            else if (language.equalsIgnoreCase("spanish")) Language.addValues(lang, Language.getSpanishFile());
            else if (language.equalsIgnoreCase("catalan")) Language.addValues(lang, Language.getCatalanFile());
            else if (language.equalsIgnoreCase("polish")) Language.addValues(lang, Language.getPolishFile());
            else if (language.equalsIgnoreCase("czech")) Language.addValues(lang, Language.getCzechFile());
            else if (language.equalsIgnoreCase("german")) Language.addValues(lang, Language.getGermanFile());

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
        r.put(Language.USER_GET_EMANCIPATOR.key, "Emancipation block gived! Place one and another over it and you'll create an emancipation grill.");
        r.put(Language.HELP_GET_GUN.key, "Get the PortalGun.");
        r.put(Language.HELP_GET_BOOTS.key, "Get the PortalBoots.");
        r.put(Language.HELP_GET_EMANCIPATOR.key, "Get the emancipation block.");
        r.put(Language.HELP_REMOVE.key, "Delete your active portals.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Delete others' active portals.");
        r.put(Language.HELP_REMOVE_ALL.key, "Delete all the active portals.");
        r.put(Language.HELP_COMPANION.key, "Spawn a companion cube.");
        r.put(Language.HELP_REPORT.key, "Send information about a problem. In the 'contact' zone set your email or discord so I can contact with you (if you don't want to set '-')");
        r.put(Language.USER_NOT_FOUND.key, "[player] not found.");
        r.put(Language.ERROR_UNKNOWN.key, "Unknown command.");
        r.put(Language.ERROR_WORLD.key, "Couldn't find the world [world].");
        r.put(Language.REPORT_SENT.key, "Report sent! Thanks for helping.");
        r.put(Language.REPORT_CONTACT_ERROR.key, "You need to put an email (something@website) or Discord (user#id) to contact. If you don't want to, then set '-'.");

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
        r.put(Language.USER_GET_EMANCIPATOR.key, "Se ha recibido el bloque emancipador! Coloca uno y otro encima y crearás una red emancipadora.");
        r.put(Language.HELP_GET_GUN.key, "Obtén la PortalGun.");
        r.put(Language.HELP_GET_BOOTS.key, "Obtén las PortalBoots.");
        r.put(Language.HELP_GET_EMANCIPATOR.key, "Obtén el bloque emancipador.");
        r.put(Language.HELP_REMOVE.key, "Elimina tus portales activos.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Elimina los portales activos de otro usuario.");
        r.put(Language.HELP_REMOVE_ALL.key, "Elimina todos los portales.");
        r.put(Language.HELP_REPORT.key, "Envia información sobre un problema. En la parte de 'contacto' pon tu email o discord para que pueda contactar contigo (si no quieres, pon '-')");
        r.put(Language.USER_NOT_FOUND.key, "El usuario [player] no se ha encontrado.");
        r.put(Language.USER_REMOVE_OTHERS.key, "Has eliminado los portales de [player].");
        r.put(Language.OTHER_USER_NO_PORTALS.key, "[player] no tiene portales abiertos.");
        r.put(Language.ERROR_UNKNOWN.key, "Comando desconocido.");
        r.put(Language.REPORT_SENT.key, "Reporte enviado! Gracias por ayudar.");
        r.put(Language.REPORT_CONTACT_ERROR.key, "Has de poner un correo (algo@web) or Discord (usuario#id) para contactar. Si no quieres, entonces pon '-'.");

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
        r.put(Language.USER_GET_EMANCIPATOR.key, "S'ha obtingut el bloc emancipador! Col·loca un i un altre a sobre i crearàs una xarxa emancipadora.");
        r.put(Language.HELP_GET_GUN.key, "Adquireix la PortalGun.");
        r.put(Language.HELP_GET_BOOTS.key, "Adquireix les PortalBoots.");
        r.put(Language.HELP_GET_EMANCIPATOR.key, "Adquireix el bloc d'emancipació.");
        r.put(Language.HELP_REMOVE.key, "Elimina els teus portals actius.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Elimina els portals actius d'un altre usuari.");
        r.put(Language.HELP_REMOVE_ALL.key, "Elimina tots els portals.");
        r.put(Language.HELP_REPORT.key, "Envia informació sobre un problema. En la part de 'contacte' posa el teu email o discord per a que pugui contactar amb tu (si no vols, posa '-')");
        r.put(Language.USER_NOT_FOUND.key, "L'usuari [player] no s'ha trobat.");
        r.put(Language.USER_REMOVE_OTHERS.key, "Has eliminat els portals de [player].");
        r.put(Language.OTHER_USER_NO_PORTALS.key, "[player] no té portals oberts.");
        r.put(Language.ERROR_UNKNOWN.key, "Comanda desconeguda.");
        r.put(Language.REPORT_SENT.key, "Report enviat! Gràcies per ajudar.");
        r.put(Language.REPORT_CONTACT_ERROR.key, "Has de posar un correu (algo@web) o Discord (usuari#id) per a contactar. Si no vols, posa '-'.");

        return r;
    }

    private static HashMap<String, Object> getPolishFile() {
        HashMap<String, Object> r = new HashMap<>();

        r.put(Language.USER_NO_PERMISSIONS.key, "Nie masz na to uprawnień!");
        r.put(Language.USER_DEATH.key, "Twoje portale zostały usunięte przez to że umarłeś.");
        r.put(Language.USER_REMOVE_ALL.key, "Usunąłeś wszystkie portale.");
        r.put(Language.USER_GET_BOOTS.key, "Buty Portalowe otrzymane!");
        r.put(Language.OTHER_USER_REMOVE.key, "[player] usunął twoje portale.");
        r.put(Language.USER_GET_EMANCIPATOR.key, "Otrzymano emancypacyjny blok! Postaw jeden i drugi na wierzchu, a stworzysz emancypacyjną sieć.");
        r.put(Language.USER_GET_GUN.key, "Działo portalowe otrzymane!");
        r.put(Language.USER_NO_PORTALS.key, "Nie masz żadnych otwartych portali.");
        r.put(Language.USER_REMOVE.key, "Usunąłeś swoje portale.");
        r.put(Language.USER_REMOVE_OTHERS.key, "Usunąłeś portale gracza [player].");
        r.put(Language.OTHER_USER_NO_PORTALS.key, "[player] nie ma żadnych otwartych portali.");
        r.put(Language.USER_NOT_FOUND.key, "[player] nie został znaleziony.");

        r.put(Language.PORTAL_OPENED.key, "[player] otworzył portal(e) w [pos].");
        r.put(Language.PORTAL_FAR.key, "Jesteś zbyt daleko aby tam stawić portal.");
        r.put(Language.PORTAL_DENIED.key, "Nie możesz otworzyć tu portalu.");
        r.put(Language.PORTAL_COLLIDING.key, "Nie możesz otworzyć dwóch portali na tym samym bloku!");

        r.put(Language.HELP_REMOVE.key, "Usuwa twoje aktywne portale.");
        r.put(Language.ERROR_UNKNOWN.key, "Nieznana komenda.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Usuń aktywne portale innych graczy.");
        r.put(Language.HELP_GET_GUN.key, "Zdobądź działo portalowe.");
        r.put(Language.HELP_GET_EMANCIPATOR.key, "Zdobądź blok wyzwoliciela.");
        r.put(Language.HELP_GET_BOOTS.key, "Zdobądź buty portalowe.");
        r.put(Language.HELP_REMOVE_ALL.key, "Usuń wszystkie aktywne portale.");
        r.put(Language.HELP_REPORT.key, "Wyślij informacje o problemie. W strefie 'kontakt' ustaw swój adres e-mail lub discord, abym mógł się z tobą skontaktować (jeśli nie chcesz ustawiać '-')");

        r.put(Language.REPORT_SENT.key, "Raport wysłany! Dziękuję za pomoc.");
        r.put(Language.REPORT_CONTACT_ERROR.key, "Aby się skontaktować, musisz podać e-mail (something@website) lub Discord (user#id). Jeśli nie chcesz, ustaw '-'.");

        return r;
    }

    private static HashMap<String, Object> getCzechFile() {
        HashMap<String, Object> r = new HashMap<>();

        r.put(Language.USER_NO_PERMISSIONS.key, "Na tohle nemáš opravnění!");
        r.put(Language.USER_DEATH.key, "Tvé potrály byly odstraněny kvůli tvé smrti.");
        r.put(Language.USER_REMOVE_ALL.key, "Všechny tvé portály byly odstraněny.");
        r.put(Language.USER_GET_BOOTS.key, "Dostal jsi Portálové boty!");
        r.put(Language.OTHER_USER_REMOVE.key, "[player] odstraníl tvé portály.");
        r.put(Language.USER_GET_EMANCIPATOR.key, "Emancipační blok byl přijat! Umístěte jeden a druhý navrch a vytvoříte emancipační síť.");
        r.put(Language.USER_GET_GUN.key, "Dostal jsi Portálovou pistoli!");
        r.put(Language.USER_NO_PORTALS.key, "Právě teď nemáš žádné aktivní portály.");
        r.put(Language.USER_REMOVE.key, "Úspěšně jsi si odstraníl všechny své portály.");
        r.put(Language.USER_REMOVE_OTHERS.key, "Odstranil jsi portály hráče [player].");
        r.put(Language.OTHER_USER_NO_PORTALS.key, "[player] nemá žádné aktivní portály.");
        r.put(Language.USER_NOT_FOUND.key, "[player] nebyl nalezen.");

        r.put(Language.PORTAL_OPENED.key, "[player] otevřel portál na [pos].");
        r.put(Language.PORTAL_FAR.key, "Tento blok se nachází příliš daleko na aktivaci portálu.");
        r.put(Language.PORTAL_DENIED.key, "Zde nelze položit portál.");
        r.put(Language.PORTAL_COLLIDING.key, "Nemůžeš položit dva portály na stejný blok!");

        r.put(Language.HELP_REMOVE.key, "Odstraní tvé aktivní portály.");
        r.put(Language.ERROR_UNKNOWN.key, "Neznámý příkaz.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Odstraní portály ostatních hráčů.");
        r.put(Language.HELP_GET_GUN.key, "Získáš Portálovou pistoli.");
        r.put(Language.HELP_GET_EMANCIPATOR.key, "Získáš emancipátorový blok.");
        r.put(Language.HELP_GET_BOOTS.key, "Získáš Portálové boty.");
        r.put(Language.HELP_REMOVE_ALL.key, "Odstraní všechny aktivní portály.");
        r.put(Language.HELP_REPORT.key, "Odešlete informace o problému. V zóně 'kontakt' nastavte svůj e-mail nebo diskor, abych se s vámi mohl spojit (pokud nechcete nastavit '-')");

        r.put(Language.REPORT_SENT.key, "Hlášení odesláno! Díky za pomoc.");
        r.put(Language.REPORT_CONTACT_ERROR.key, "Chcete-li kontaktovat, musíte zadat e-mail (něco@web) nebo Discord (uživatele#ID). Pokud nechcete, nastavte '-'.");

        return r;
    }

    private static HashMap<String, Object> getGermanFile() {
        HashMap<String, Object> r = new HashMap<>();

        r.put(Language.USER_NO_PERMISSIONS.key, "Du hast keine Rechte!");
        r.put(Language.USER_DEATH.key, "Deine Portale haben sich wegen deines Todes geschlossen.");
        r.put(Language.USER_REMOVE_ALL.key, "Du hast alle Portale geschlossen.");
        r.put(Language.USER_GET_BOOTS.key, "Du hast die PortalBoots erhalten!");
        r.put(Language.OTHER_USER_REMOVE.key, "[player] hat deine Portale geschlossen.");
        r.put(Language.USER_GET_EMANCIPATOR.key, "Du hast den Emancipation block erhalten! Platziere 2 aufeinander um die Portale zu benutzen.");
        r.put(Language.USER_GET_GUN.key, "Du hast die PortalGun erhalten.");
        r.put(Language.USER_NO_PORTALS.key, "Du hast grade keine offende Portale.");
        r.put(Language.USER_REMOVE.key, "Du hast erfolgreich deine Portale geschlossen.");
        r.put(Language.USER_REMOVE_OTHERS.key, "Du hast die Portale von [player] geschlossen.");
        r.put(Language.OTHER_USER_NO_PORTALS.key, "[player] hat keine offende Portale.");
        r.put(Language.USER_NOT_FOUND.key, "[player] nicht gefunden.");

        r.put(Language.PORTAL_OPENED.key, "[player] hat ein Portal bei [pos] geoefnet.");
        r.put(Language.PORTAL_FAR.key, "Der Block ist zu weit entfernt.");
        r.put(Language.PORTAL_DENIED.key, "Hier kannst du kein Portal oefnnen.");
        r.put(Language.PORTAL_COLLIDING.key, "Du kannst nicht beide Portale auf denselben Block setzen.");

        r.put(Language.HELP_REMOVE.key, "Loescht deine aktiven Portale.");
        r.put(Language.ERROR_UNKNOWN.key, "Unbekannter Befehl.");
        r.put(Language.HELP_REMOVE_OTHERS.key, "Delete others' active portals.");
        r.put(Language.HELP_GET_GUN.key, "Erhalte die PortalGun.");
        r.put(Language.HELP_GET_EMANCIPATOR.key, "Erhalte den emancipation block.");
        r.put(Language.HELP_GET_BOOTS.key, "Erhalte die PortalBoots.");
        r.put(Language.HELP_REMOVE_ALL.key, "Loescht alle aktiven Portale.");
        r.put(Language.HELP_REPORT.key, "Sendet ein Bug Report an die Entwickler. Du must eine E-Mail (something@website) eingeben oder ein Discord (user#id) Benutzername. Wenn du es nicht willst, denn schreibe '-'.");

        r.put(Language.REPORT_SENT.key, "Report gesendet. Danke fuer deine Hilfe.");
        r.put(Language.REPORT_CONTACT_ERROR.key, "Du must eine E-Mail (something@website) eingeben oder ein Discord (user#id) Benutzername. Wenn du es nicht willst, denn schreibe '-'.");

        return r;
    }
}

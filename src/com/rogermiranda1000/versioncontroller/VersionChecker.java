package com.rogermiranda1000.versioncontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class VersionChecker {
    private static final String PLUGIN_VERSION_URL = "https://api.spigotmc.org/legacy/update.php?resource={id}";

    public static String getVersion(String id) throws IOException {
        InputStream inputStream = new URL(VersionChecker.PLUGIN_VERSION_URL.replace("{id}", id)).openStream();
        Scanner scanner = new Scanner(inputStream);
        if (scanner.hasNext()) {
            String version = scanner.next();
            scanner.close();
            return version;
        }
        else throw new IOException("No version returned");
    }

    /**
     * It compares two versions
     * @param current The current plugin version
     * @param comparing The version to compare (getted from spigot; the last one)
     * @return If the current version is lower than the newest one
     * @throws NumberFormatException If the strings are not integers with dots
     */
    public static boolean isLower(String current, String comparing) throws NumberFormatException {
        Version currentVersion = new Version(current), comparingVersion = new Version(comparing);
        return (currentVersion.compareTo(comparingVersion) < 0);
    }
}

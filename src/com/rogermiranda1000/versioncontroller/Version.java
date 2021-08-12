package com.rogermiranda1000.versioncontroller;

import org.jetbrains.annotations.NotNull;

/**
 * A Minecraft version (ex: 1.8 or 1.12.2)
 */
public class Version implements Comparable<Version> {
    private final byte []version;

    /**
     * It parses a string
     * @param version Version (ex: 1.8 or 1.12.2)
     * @throws NumberFormatException The version is not a number
     */
    public Version(String version) throws NumberFormatException {
        this.version = new byte[3];

        String []currentParams = version.split("\\.");
        for (int x = 0; x < 3; x++) {
            if (x < currentParams.length) this.version[x] = Byte.parseByte(currentParams[x]);
            else this.version[x] = 0;
        }
    }

    /**
     * It compares two versions
     * @param o Object to compare
     * @return   0: if (this == o)
     *          <0: if (this < o)
     *          >0: if (this > o)
     */
    @Override
    public int compareTo(@NotNull Version o) {
        int tmp, x = 0;
        do {
            tmp = this.version[x] - o.version[x];
            x++;
        } while (x < 3 && tmp == 0);
        return tmp;
    }
}

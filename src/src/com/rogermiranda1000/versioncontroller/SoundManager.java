package com.rogermiranda1000.versioncontroller;

import org.bukkit.Sound;

public class SoundManager {
    private static Sound teleportSound;
    private static final Sound createSound;

    // static class constructor
    static {
        createSound = Sound.ENTITY_SLIME_JUMP;

        try {
            // version < 1.9
            if(VersionController.getVersion()<9) teleportSound = Sound.valueOf("ENDERMAN_TELEPORT");
                // version >= 1.9
            else teleportSound = Sound.valueOf("ENTITY_SHULKER_TELEPORT");
        } catch(IllegalArgumentException IAEx) {
            IAEx.printStackTrace();
            teleportSound = null;
        }
    }

    public static Sound getTeleportSound() {
        return SoundManager.teleportSound;
    }

    public static Sound getCreateSound() {
        return SoundManager.createSound;
    }
}

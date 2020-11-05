package com.rogermiranda1000.portalgun.versioncontroller;

import org.bukkit.Sound;

public class SoundManager {
    private static Sound teleportSound;

    // static class constructor
    static {
        try {
            // version < 1.11
            if(VersionController.getVersion()<11) teleportSound = Sound.valueOf("ENTITY_SHULKER_TELEPORT");
                // version >= 1.1
            else teleportSound = Sound.valueOf("ENTITY_ENDERMAN_TELEPORT");
        } catch(IllegalArgumentException IAEx) {
            IAEx.printStackTrace();
            teleportSound = null;
        }
    }

    public static Sound getTeleportSound() {
        return SoundManager.teleportSound;
    }
}

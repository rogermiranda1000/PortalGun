package com.rogermiranda1000.portalgun.items;

import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResourcepackedItemFactory {
    public static ResourcepackedItem createItem(Material mat, String name, int identifier, @Nullable List<String> lore) {
        if (VersionController.version.compareTo(Version.MC_1_14) >= 0) return new ResourcepackedCMDItem(mat, name, identifier, lore);
        else return new ResourcepackedDamagedItem(mat, name, identifier, lore);
    }

    public static ResourcepackedItem createItem(Material mat, String name, int identifier) {
        return ResourcepackedItemFactory.createItem(mat, name, identifier, null);
    }
}

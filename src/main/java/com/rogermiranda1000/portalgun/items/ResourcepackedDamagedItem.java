package com.rogermiranda1000.portalgun.items;

import com.rogermiranda1000.portalgun.PortalGun;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResourcepackedDamagedItem extends ResourcepackedItem {
    public ResourcepackedDamagedItem(Material mat, String name, int damage, @Nullable List<String> lore) throws IllegalArgumentException {
        super(mat, name, lore);

        VersionController.get().setUnbreakable(this);
        VersionController.get().setDurability(this, damage);
        //meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    }

    public ResourcepackedDamagedItem(Material mat, String name, int damage) {
        this(mat, name, damage, null);
    }

    public int getIdentifier() {
        return VersionController.get().getDurability(this);
    }
}

package com.rogermiranda1000.portalgun.items;

import com.rogermiranda1000.portalgun.PortalGun;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResourcepackedCMDItem extends ResourcepackedItem {
    public ResourcepackedCMDItem(Material mat, String name, int customModelData, @Nullable List<String> lore) {
        super(mat, name, lore);

        ItemMeta meta = this.getItemMeta();
        meta.setCustomModelData(customModelData);
        this.setItemMeta(meta);
    }

    public ResourcepackedCMDItem(Material mat, String name, int customModelData) {
        this(mat, name, customModelData, null);
    }

    public int getIdentifier() {
        return PortalGun.item.getItemMeta().getCustomModelData();
    }
}

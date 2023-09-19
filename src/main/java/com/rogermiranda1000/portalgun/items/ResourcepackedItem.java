package com.rogermiranda1000.portalgun.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ResourcepackedItem extends ItemStack {
    public ResourcepackedItem(ItemStack item, String name, @Nullable List<String> lore) {
        super(item);

        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        this.setItemMeta(meta);
    }

    public ResourcepackedItem(Material mat, String name, @Nullable List<String> lore) {
        this(new ItemStack(mat), name, lore);
    }

    public ResourcepackedItem(Material mat, String name) {
        this(mat, name, null);
    }

    public abstract int getIdentifier();
}

package com.rogermiranda1000.portalgun.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CompanionCube extends Cube {
    public CompanionCube(@NotNull Location spawnAt) {
        super(spawnAt);
    }

    @Override
    protected void applyTexture(ArmorStand companionCube) {
        companionCube.setHelmet(new ItemStack(Material.IRON_BLOCK)); // TODO custom texture
    }
}

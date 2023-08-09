package com.rogermiranda1000.portalgun.cubes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RedirectionCube extends Cube {
    public RedirectionCube(@NotNull Location spawnAt) {
        super(spawnAt);
    }

    @Override
    protected void applyTexture(ArmorStand redirectionCube) {
        redirectionCube.setHelmet(new ItemStack(Material.GLASS)); // TODO custom texture
    }
}

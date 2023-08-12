package com.rogermiranda1000.portalgun.refactored.portal.cubes;

import com.rogermiranda1000.portalgun.cubes.Cube;
import com.rogermiranda1000.portalgun.refactored.geometry.Line;
import com.rogermiranda1000.portalgun.refactored.geometry.Vector;
import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluenced;
import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluencer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RedirectionCube extends Cube implements TrajectoryInfluencer {
    public RedirectionCube(@NotNull Location spawnAt) {
        super(spawnAt);
    }

    @Override
    protected void applyTexture(ArmorStand redirectionCube) {
        redirectionCube.setHelmet(new ItemStack(Material.GLASS)); // TODO custom texture
    }

    @Override
    public Line modifyTrajectory(Line trajectory) {
        return trajectory;
    }
}

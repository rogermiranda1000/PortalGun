package com.rogermiranda1000.portalgun.refactored.cubes;

import com.rogermiranda1000.portalgun.cubes.Cube;
import com.rogermiranda1000.portalgun.refactored.geometry.Line;
import com.rogermiranda1000.portalgun.refactored.geometry.Vector;
import com.rogermiranda1000.portalgun.refactored.portals.TrajectoryInfluencer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RedirectionCube extends Cube implements TrajectoryInfluencer<Double> {
    public RedirectionCube(@NotNull Location spawnAt) {
        super(spawnAt);
    }

    @Override
    protected void applyTexture(ArmorStand redirectionCube) {
        redirectionCube.setHelmet(new ItemStack(Material.GLASS)); // TODO custom texture
    }

    @Override
    public Line<Double> getNewTrajectory(Vector<Double> in) {
        return new Line<>(
                new Double[]{},
                new Double[]{}
        );
    }
}

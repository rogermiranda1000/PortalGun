package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.cubes.CompanionCube;
import com.rogermiranda1000.portalgun.cubes.Cube;
import com.rogermiranda1000.portalgun.cubes.Cubes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class onEmancipator {

    public void onEntityGoesThroughEmancipationGrid(Entity e) {
        Cube cube;
        if (e instanceof Player) {
            Entity picked = onPortalgunEntity.getEntityPicked((Player) e);
            if (picked != null) onEntityGoesThroughEmancipationGrid(picked); // it's like the other entity has come with the player
        }
        else if ((cube = Cubes.getCube(e)) != null) Cubes.destroyCompanionCube(cube);
    }
}

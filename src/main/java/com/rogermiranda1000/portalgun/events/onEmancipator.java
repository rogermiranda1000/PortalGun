package com.rogermiranda1000.portalgun.events;

import com.rogermiranda1000.portalgun.blocks.CompanionCubes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class onEmancipator {

    public void onEntityGoesThroughEmancipationGrid(Entity e) {
        if (e instanceof Player) {
            Entity picked = onPortalgunEntity.getEntityPicked((Player) e);
            onEntityGoesThroughEmancipationGrid(picked); // it's like the other entity has come with the player
        }
        else if (CompanionCubes.isCompanionCube(e)) CompanionCubes.destroyCompanionCube(e);
    }
}

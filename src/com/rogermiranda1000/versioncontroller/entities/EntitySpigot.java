package com.rogermiranda1000.versioncontroller.entities;

import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class EntitySpigot implements EntityManager {
    @Override
    public @NotNull Vector getVelocity(Entity e) {
        return e.getVelocity();
    }

    @Override
    public @NotNull Vector getVelocity(PlayerMoveEvent e) {
        return e.getPlayer().getVelocity();
    }
}

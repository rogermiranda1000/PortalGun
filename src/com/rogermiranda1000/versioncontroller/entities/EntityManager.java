package com.rogermiranda1000.versioncontroller.entities;

import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface EntityManager {
    @NotNull
    Vector getVelocity(Entity e);

    @NotNull
    Vector getVelocity(PlayerMoveEvent e);
}

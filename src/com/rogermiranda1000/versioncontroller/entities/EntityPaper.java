package com.rogermiranda1000.versioncontroller.entities;

import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityPaper implements EntityManager {
    @Override
    public @NotNull Vector getVelocity(Entity e) {
        return new Vector(0,0,0);// TODO
    }

    @Override
    public @NotNull Vector getVelocity(PlayerMoveEvent e) {
        if (e.getTo() == null || !Objects.equals(e.getTo().getWorld(), e.getFrom().getWorld())) return new Vector(0,0,0); // not the same world
        return (e.getTo().clone()).subtract(e.getFrom()).toVector();
    }
}

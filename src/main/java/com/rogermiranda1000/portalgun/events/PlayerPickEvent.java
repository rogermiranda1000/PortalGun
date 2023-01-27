package com.rogermiranda1000.portalgun.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPickEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Entity entityPicked;
    private Result userPickedEvent;

    public PlayerPickEvent(@NotNull Player who, @NotNull Entity entity) {
        super(who);
        this.entityPicked = entity;
        this.userPickedEvent = Result.DEFAULT;
    }

    public boolean isCancelled() {
        return this.userPickedEvent == Result.DENY;
    }

    public void setCancelled(boolean cancel) {
        this.userPickedEvent = (cancel ? Result.DENY : Result.DEFAULT);
    }

    public Entity getEntityPicked() {
        return this.entityPicked;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

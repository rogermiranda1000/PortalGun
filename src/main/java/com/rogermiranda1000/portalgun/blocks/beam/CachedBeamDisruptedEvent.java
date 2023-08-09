package com.rogermiranda1000.portalgun.blocks.beam;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CachedBeamDisruptedEvent implements BeamDisruptedEvent {
    private Object lastCalled;
    private Vector lastVector;
    private final BeamDisruptedEvent callback;

    public CachedBeamDisruptedEvent(BeamDisruptedEvent callback) {
        this.callback = callback;
        this.lastCalled = null;
    }

    @Override
    public void onBeamDisrupted(@NotNull Entity e, Location hit) {
        if (e.equals(this.lastCalled) && hit.toVector().distance(this.lastVector) <= 1e-6) return;

        this.lastCalled = e;
        this.lastVector = hit.toVector();
        this.callback.onBeamDisrupted(e, hit);
    }

    @Override
    public void onBeamDisrupted(@NotNull Block b, Location hit) {
        if (b.equals(this.lastCalled) && hit.toVector().distance(this.lastVector) <= 1e-6) return;

        this.lastCalled = b;
        this.lastVector = hit.toVector();
        this.callback.onBeamDisrupted(b, hit);
    }
}

package com.rogermiranda1000.portalgun.blocks.beam;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class CachedBeamDisruptedEvent implements BeamDisruptedEvent {
    private Object lastCalled;
    private final BeamDisruptedEvent callback;

    public CachedBeamDisruptedEvent(BeamDisruptedEvent callback) {
        this.callback = callback;
        this.lastCalled = null;
    }

    @Override
    public void onBeamDisrupted(@NotNull Entity e) {
        if (e.equals(this.lastCalled)) return;

        this.lastCalled = e;
        this.callback.onBeamDisrupted(e);
    }

    @Override
    public void onBeamDisrupted(@NotNull Block b) {
        if (b.equals(this.lastCalled)) return;

        this.lastCalled = b;
        this.callback.onBeamDisrupted(b);
    }
}

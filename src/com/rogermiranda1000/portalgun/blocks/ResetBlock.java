package com.rogermiranda1000.portalgun.blocks;

import org.bukkit.Location;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ResetBlock {
    private final Location position;
    private ResetBlock top, bottom;
    private Location []particleLocations;

    protected ResetBlock(Location position) {
        this.position = position;
        this.searchOnTop(false);
    }

    /**
     * Called before the block is going to be deleted
     */
    public void removed() {
        if (this.top != null) this.top.setBottom(null); // if there's another block, 'unlink' will set the new bottom
        if (this.bottom != null) this.bottom.unlink();
    }

    /**
     * The block linked won't be anymore
     */
    public void unlink() {
        this.searchOnTop(true);
    }

    /**
     * Changes the current top and bottom
     * @param ignoreCurrent If the current top will be removed
     */
    public void searchOnTop(boolean ignoreCurrent) {
        final AtomicReference<ResetBlock> newTop = new AtomicReference<>(null);
        final AtomicInteger closest = new AtomicInteger(Integer.MAX_VALUE);

        ResetBlocks.getInstance().getBlocksLackingCoordinate(this.position.getWorld(), this.position.getBlockX(), null, this.position.getBlockZ(), e -> {
            int height = e.getValue().getBlockY();
            if ((!ignoreCurrent || e.getKey() != top) && height > position.getBlockY() && height < closest.get()) {
                closest.set(height);
                newTop.set(e.getKey());
            }
        });

        this.setTop(newTop.get());
        final AtomicReference<ResetBlock> newBottom = new AtomicReference<>();
        if (this.top != null) {
            newBottom.set(this.top.bottom);
            this.top.setBottom(this);
        }
        else {
            closest.set(Integer.MIN_VALUE);
            ResetBlocks.getInstance().getBlocksLackingCoordinate(this.position.getWorld(), this.position.getBlockX(), null, this.position.getBlockZ(), e -> {
                int height = e.getValue().getBlockY();
                if (height < position.getBlockY() && height > closest.get()) {
                    closest.set(height);
                    newBottom.set(e.getKey());
                }
            });
        }

        this.setBottom(newBottom.get());
        if (this.bottom != null) this.bottom.setTop(this);
    }

    private void setTop(ResetBlock top) {
        if (top != null) System.out.println(this.position.toString() + " new top: " + top.getPosition().toString());
        this.top = top;
        this.particleLocations = calculateParticles();
    }

    private void setBottom(ResetBlock bottom) {
        if (bottom != null) System.out.println(this.position.toString() + " new bottom: " + bottom.getPosition().toString());
        this.bottom = bottom;
    }

    public void playParticle() {

    }

    private Location[] calculateParticles() {
        return null;
    }

    public Location getPosition() {
        return this.position;
    }
}

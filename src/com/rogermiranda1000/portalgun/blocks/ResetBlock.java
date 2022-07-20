package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.versioncontroller.particles.ParticleEntity;
import org.bukkit.Location;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ResetBlock {
    private static ParticleEntity particle;

    private final Location position;
    private ResetBlock top, bottom;

    protected ResetBlock(Location position) {
        this.position = position;
        this.searchOnTop();
    }

    /**
     * Called before the block is going to be deleted
     */
    public void removed() {
        if (this.top != null) this.top.setBottom(this.bottom); // if there's another block, 'unlink' will set the new bottom
        if (this.bottom != null) this.bottom.setTop(this.top);
    }

    /**
     * Changes the current top and bottom
     */
    public void searchOnTop() {
        final AtomicReference<ResetBlock> newTop = new AtomicReference<>(null);
        final AtomicInteger closest = new AtomicInteger(Integer.MAX_VALUE);
        ResetBlocks.getInstance().getBlocksLackingCoordinate(this.position.getWorld(), this.position.getBlockX(), null, this.position.getBlockZ(), e -> {
            int height = e.getValue().getBlockY();
            if (height > this.position.getBlockY() && height < closest.get()) {
                closest.set(height);
                newTop.set(e.getKey());
            }
        });
        this.setTop(newTop.get());

        final AtomicReference<ResetBlock> newBottom = new AtomicReference<>(null);
        if (this.top != null) {
            newBottom.set(this.top.bottom);
            this.top.setBottom(this);
        }
        else {
            closest.set(Integer.MIN_VALUE);
            ResetBlocks.getInstance().getBlocksLackingCoordinate(this.position.getWorld(), this.position.getBlockX(), null, this.position.getBlockZ(), e -> {
                int height = e.getValue().getBlockY();
                if (height < this.position.getBlockY() && height > closest.get()) {
                    closest.set(height);
                    newBottom.set(e.getKey());
                }
            });
        }

        this.setBottom(newBottom.get());
        if (this.bottom != null) this.bottom.setTop(this);
    }

    private void setTop(ResetBlock top) {
        //if (top != null) System.out.println(this.position.toString() + " new top: " + top.getPosition().toString());
        this.top = top;
    }

    private void setBottom(ResetBlock bottom) {
        //if (bottom != null) System.out.println(this.position.toString() + " new bottom: " + bottom.getPosition().toString());
        this.bottom = bottom;
    }

    public static void setParticle(ParticleEntity particle) {
        ResetBlock.particle = particle;
    }

    private static float getRandomBetweenNumbers(Random generator, float min, float max) {
        return min + generator.nextFloat()*(max - min);
    }

    public void playParticles(Random generator) {
        int height = this.getZoneHeight();
        if (height == 0 || ResetBlock.particle == null) return;

        for (int i = 0; i < 2*height; i++) {
            double x = this.position.getBlockX() + generator.nextFloat(),
                    y = ResetBlock.getRandomBetweenNumbers(generator, this.position.getBlockY()+1, this.position.getBlockY()+1+height),
                    z = this.position.getBlockZ() + generator.nextFloat();
            ResetBlock.particle.playParticle(this.position.getWorld(), new Location(this.position.getWorld(), x, y, z));
        }
    }

    /**
     * Top block's y - current block's y
     * @return Difference of height between blocks (0 if top is null)
     */
    public int getZoneHeight() {
        if (this.top == null) return 0;
        return this.top.position.getBlockY() - this.position.getBlockY() - 1;
    }

    public Location getPosition() {
        return this.position;
    }
}

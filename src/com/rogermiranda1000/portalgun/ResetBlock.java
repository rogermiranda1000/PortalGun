package com.rogermiranda1000.portalgun;

import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ResetBlock {
    /**
     * The ResetBlock type. Must be a TileState
     */
    public static final BlockType resetBlockType = VersionController.get().getMaterial(Material.DISPENSER.name());

    private static final NamespacedKey key = new NamespacedKey(PortalGun.plugin, "resetBlocks");

    private ResetBlock upperLink;
    private final Location position;

    private ResetBlock(Block b) {
        this.position = b.getLocation();
        // TODO check upperLink

        BlockState blockState = b.getState();
        TileState tileState = (TileState) blockState;
        PersistentDataContainer container = tileState.getPersistentDataContainer();
        container.set(key, PersistentDataType.BYTE, (byte)1);
        tileState.update();
    }

    public void blockBreaked() {

    }

    @Nullable
    public static ResetBlock instantiateResetBlock(Block b) {
        if (!VersionController.get().getObject(b).equals(ResetBlock.resetBlockType)) return null;
        return new ResetBlock(b);
    }
}

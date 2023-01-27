package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.helper.RogerPlugin;
import com.rogermiranda1000.helper.blocks.ComplexStoreConversion;
import com.rogermiranda1000.helper.blocks.CustomBlock;
import com.rogermiranda1000.helper.blocks.file.BasicLocation;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class ResetBlocks extends CustomBlock<ResetBlock> {
    public static class StoreResetBlock implements ComplexStoreConversion<ResetBlock, BasicLocation> {
        public StoreResetBlock() {}

        @Override
        public Function<ResetBlock, BasicLocation> storeName() {
            return in->new BasicLocation(in.getPosition());
        }

        @Override
        public Function<BasicLocation, ResetBlock> loadName() {
            return in->new ResetBlock(in.getLocation());
        }

        @Override
        public Class<BasicLocation> getOutputClass() { return BasicLocation.class; }
    }

    public static final ItemStack resetBlockItem = ResetBlocks.getResetBlockItem();
    private static final BlockType resetBlockType = VersionController.get().getObject(resetBlockItem);
    private static final String id = "ResetBlocks";

    private static ItemStack getResetBlockItem() {
        ItemStack r = new ItemStack(Material.IRON_BLOCK);
        ItemMeta meta = r.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Emancipation Block");
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        r.setItemMeta(meta);
        return r;
    }

    public ResetBlocks(RogerPlugin plugin) {
        super(plugin, ResetBlocks.id, e -> (BlockEvent.class.isAssignableFrom(e.getClass()) && VersionController.get().getObject(((BlockEvent)e).getBlock()).equals(ResetBlocks.resetBlockType)
                && (!BlockPlaceEvent.class.isAssignableFrom(e.getClass()) || VersionController.get().sameItem(((BlockPlaceEvent)e).getItemInHand(), ResetBlocks.resetBlockItem))), false, true, new StoreResetBlock());
    }

    @Override
    public @NotNull ResetBlock onCustomBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        ResetBlock r = new ResetBlock(blockPlaceEvent.getBlock().getLocation());
        r.update();
        return r;
    }

    @Override
    public boolean onCustomBlockBreak(BlockBreakEvent blockBreakEvent, ResetBlock block) {
        block.removed();
        return false;
    }

    public boolean insideResetBlock(final Location loc) {
        final AtomicBoolean found = new AtomicBoolean(false);
        this.getAllBlocks(e -> {
            if (e.getKey().insideRegion(loc)) found.set(true);
        });
        return found.get();
    }

    public void updateAllBlocks() {
        this.getAllBlocks(e -> e.getKey().update());
    }

    private static final Random generator = new Random();
    public void playAllParticles() {
        this.getAllBlocks(e -> {
            if (e.getValue().getChunk().isLoaded()) e.getKey().playParticles(ResetBlocks.generator);
        });
    }

    private static ResetBlocks instance = null;
    public static ResetBlocks setInstance(ResetBlocks instance) {
        ResetBlocks.instance = instance;
        return instance;
    }

    public static ResetBlocks getInstance() {
        return ResetBlocks.instance;
    }
}

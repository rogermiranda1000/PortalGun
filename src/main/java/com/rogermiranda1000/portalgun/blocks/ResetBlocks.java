package com.rogermiranda1000.portalgun.blocks;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.rogermiranda1000.helper.RogerPlugin;
import com.rogermiranda1000.helper.blocks.ComplexStoreConversion;
import com.rogermiranda1000.helper.blocks.CustomBlock;
import com.rogermiranda1000.helper.blocks.file.BasicLocation;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;

public class ResetBlocks extends CustomBlock<ResetBlock> implements Listener {
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

    private void recalculateDisabled(Location eventOrigin) {
        HashSet<ResetBlock> updatedBlocks = this.disabling.remove(eventOrigin);
        if (updatedBlocks == null) return; // shouldn't happen

        // don't make assumptions; they may be powered on multiple points
        // (if the power origin is the same it will be called again, and then they will be enabled)
        for (ResetBlock rb : updatedBlocks) {
            boolean stillPowered = this.disabling.values().stream().anyMatch(set -> set.contains(rb));
            if (!stillPowered) rb.enable();
        }
    }

    private HashMap<Location,HashSet<ResetBlock>> disabling = new HashMap<>();
    private void disableBlock(Location eventOrigin, ResetBlock block) {
        if (!this.disabling.containsKey(eventOrigin)) this.disabling.put(eventOrigin, new HashSet<>());

        boolean updated = this.disabling.get(eventOrigin).add(block);

        if (updated) {
            block.disable();

            // propagate on adjacent blocks
            Location blockPosition = block.getPosition().getBlock().getLocation();
            Location[] adjacents = new Location[]{
                    blockPosition.clone().add(1, 0, 0),
                    blockPosition.clone().add(-1, 0, 0),
                    blockPosition.clone().add(0, 0, 1),
                    blockPosition.clone().add(0, 0, -1)
            };
            for (Location adjacent : adjacents) {
                ResetBlock toDisable = this.getBlock(adjacent);
                if (toDisable == null) continue; // not a ResetBlock

                this.disableBlock(eventOrigin, toDisable);
            }
        }
    }

    private static Rectangle powerBlockInfluence(Location loc) {
        Location min = loc.clone().subtract(1, 1, 1),
                max = loc.clone().add(1, 0, 1);
        Rectangle region = Rectangle.create(
                CustomBlock.getPointWithMargin(min).mins(),
                CustomBlock.getPointWithMargin(max).maxes()
        );
        return region;
    }

    @EventHandler
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        final Location loc = event.getBlock().getLocation();

        // NOTE: this event won't be called if a redstone dust breaks, so in that case the portal won't re-open
        if (event.getNewCurrent() == 0) this.recalculateDisabled(loc);
        else {
            // search a rectangle in the powered block;
            synchronized(this) {
                this.blocks.search(ResetBlocks.powerBlockInfluence(loc)).forEach(e -> disableBlock(loc, e.value()));
            }
        }
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

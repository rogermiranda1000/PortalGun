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
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Restarter");
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
        // TODO get upper block (if any)
        // TODO check if perturbed previous block
        System.out.println(blockPlaceEvent.getBlock().getLocation().toString());
        Location loc = blockPlaceEvent.getBlock().getLocation();
        this.getBlocksLackingCoordinate(loc.getWorld(), loc.getBlockX(), null, loc.getBlockZ(), r -> System.out.println("- " + r.getValue()));
        return new ResetBlock(loc);
    }

    @Override
    public boolean onCustomBlockBreak(BlockBreakEvent blockBreakEvent, ResetBlock block) {
        // TODO unlink bottom block
        // TODO check if new region can be created
        System.out.println(blockBreakEvent.getBlock().getLocation().toString());
        System.out.println(block.getPosition().toString());
        return false;
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

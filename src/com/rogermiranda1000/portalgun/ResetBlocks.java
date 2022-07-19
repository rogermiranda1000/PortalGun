package com.rogermiranda1000.portalgun;

import com.rogermiranda1000.helper.RogerPlugin;
import com.rogermiranda1000.helper.blocks.CustomBlock;
import com.rogermiranda1000.helper.blocks.Ignored;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ResetBlocks extends CustomBlock<Ignored> {
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
        super(plugin, ResetBlocks.id, e -> (e.getClass().isAssignableFrom(BlockEvent.class) && VersionController.get().getObject(((BlockEvent)e).getBlock()).equals(ResetBlocks.resetBlockType)
                && (!e.getClass().isAssignableFrom(BlockPlaceEvent.class) || VersionController.get().sameItem(((BlockPlaceEvent)e).getItemInHand(), ResetBlocks.resetBlockItem))), false, true, new Ignored.StoreIgnored());
    }

    @Override
    public @NotNull Ignored onCustomBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        // TODO get upper block (if any)
        // TODO check if perturbed previous block
        System.out.println(blockPlaceEvent.getBlock().getLocation().toString());
        return Ignored.get();
    }

    @Override
    public boolean onCustomBlockBreak(BlockBreakEvent blockBreakEvent, Ignored ignored) {
        // TODO unlink bottom block
        // TODO check if new region can be created
        System.out.println(blockBreakEvent.getBlock().getLocation().toString());
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

package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.helper.RogerPlugin;
import com.rogermiranda1000.helper.blocks.ComplexStoreConversion;
import com.rogermiranda1000.helper.blocks.CustomBlock;
import com.rogermiranda1000.helper.blocks.file.BasicLocation;
import com.rogermiranda1000.portalgun.utils.stairs.DirectionGetter;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class ThermalReceivers extends CustomBlock<ThermalReceiver> {
    public static class StoreThermalReceiver implements ComplexStoreConversion<ThermalReceiver, BasicLocation> {
        public StoreThermalReceiver() {}

        @Override
        public Function<ThermalReceiver, BasicLocation> storeName() {
            return in->new BasicLocation(in.getPosition());
        }

        @Override
        public Function<BasicLocation, ThermalReceiver> loadName() {
            return (in) -> {
                Location loc = in.getLocation();
                Vector facing = new Vector(1,0,0);
                try {
                    Block block = loc.getWorld().getBlockAt(loc);
                    facing = DirectionGetter.getDirection(block).getFacingVector();
                } catch (IllegalArgumentException | NullPointerException ignore) {}
                ThermalReceiver thermalReceiver = new ThermalReceiver(loc, facing);
                thermalReceiver.decorate();
                return thermalReceiver;
            };
        }

        @Override
        public Class<BasicLocation> getOutputClass() { return BasicLocation.class; }
    }

    public static final ItemStack thermalReceiverItem = ThermalReceivers.getThermalReceiverItem();
    private static final String id = "ThermalReceiver";

    private static ItemStack getThermalReceiverItem() {
        ItemStack r = new ItemStack(Material.QUARTZ_STAIRS);
        ItemMeta meta = r.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Thermal Discouragement Receiver");
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        r.setItemMeta(meta);
        return r;
    }

    public ThermalReceivers(RogerPlugin plugin) {
        super(plugin, ThermalReceivers.id, e -> (BlockEvent.class.isAssignableFrom(e.getClass()) && ((BlockEvent)e).getBlock().getType().equals(ThermalReceivers.thermalReceiverItem.getType())
                && (!BlockPlaceEvent.class.isAssignableFrom(e.getClass()) || VersionController.get().sameItem(((BlockPlaceEvent)e).getItemInHand(), ThermalReceivers.thermalReceiverItem))), false, true, new ThermalReceivers.StoreThermalReceiver());
    }

    @Override
    public @NotNull ThermalReceiver onCustomBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Vector facing = new Vector(1,0,0);
        try {
            facing = DirectionGetter.getDirection(blockPlaceEvent.getBlock()).getFacingVector();
        } catch (IllegalArgumentException ignore) {}
        ThermalReceiver thermalReceiver = new ThermalReceiver(blockPlaceEvent.getBlock().getLocation(), facing);
        thermalReceiver.decorate();
        return thermalReceiver;
    }

    @Override
    public boolean onCustomBlockBreak(BlockBreakEvent blockBreakEvent, ThermalReceiver thermalReceiver) {
        thermalReceiver.destroy();
        return false;
    }

    public void destroyAll() {
        this.getAllBlocks(e -> e.getKey().destroy());
    }

    public void unpowerAll() {
        this.getAllBlocks(e -> e.getKey().forceUnpower());
    }

    public boolean isDecorate(final Entity entity) {
        final AtomicBoolean matches = new AtomicBoolean(false);
        this.getAllBlocks(e -> matches.set(e.getKey().isDecorate(entity) || matches.get()));
        return matches.get();
    }

    @Override
    @Nullable
    public synchronized ThermalReceiver getBlock(Location loc) {
        Material blockType = loc.getBlock().getType();
        if (!blockType.equals(ThermalReceivers.thermalReceiverItem.getType())
                && !blockType.equals(Material.REDSTONE_BLOCK)) return null; // not a receiver
        return super.getBlock(loc);
    }

    private static ThermalReceivers instance = null;
    public static ThermalReceivers setInstance(ThermalReceivers instance) {
        ThermalReceivers.instance = instance;
        return instance;
    }

    public static ThermalReceivers getInstance() {
        return ThermalReceivers.instance;
    }
}

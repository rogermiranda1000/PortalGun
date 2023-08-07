package com.rogermiranda1000.portalgun.blocks;

import com.rogermiranda1000.helper.RogerPlugin;
import com.rogermiranda1000.helper.blocks.ComplexStoreConversion;
import com.rogermiranda1000.helper.blocks.CustomBlock;
import com.rogermiranda1000.helper.blocks.file.BasicLocation;
import com.rogermiranda1000.portalgun.utils.stairs.DirectionGetter;
import com.rogermiranda1000.versioncontroller.VersionController;
import com.rogermiranda1000.versioncontroller.blocks.BlockType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ThermalBeams extends CustomBlock<ThermalBeam> {
    public static class StoreThermalBeam implements ComplexStoreConversion<ThermalBeam, BasicLocation> {
        public StoreThermalBeam() {}

        @Override
        public Function<ThermalBeam, BasicLocation> storeName() {
            return in->new BasicLocation(in.getPosition());
        }

        @Override
        public Function<BasicLocation, ThermalBeam> loadName() {
            return (in) -> {
                Location loc = in.getLocation();
                Vector facing = new Vector(1,0,0);
                try {
                    Block block = loc.getWorld().getBlockAt(loc);
                    facing = DirectionGetter.getDirection(block).getFacingVector();
                } catch (IllegalArgumentException | NullPointerException ignore) {}
                return new ThermalBeam(loc, facing);
            };
        }

        @Override
        public Class<BasicLocation> getOutputClass() { return BasicLocation.class; }
    }

    public static final ItemStack thermalBeamItem = ThermalBeams.getThermalBeamItem();
    private static final String id = "ThermalBeams";

    private static ItemStack getThermalBeamItem() {
        ItemStack r = new ItemStack(Material.QUARTZ_STAIRS);
        ItemMeta meta = r.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Thermal Discouragement Beam");
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        r.setItemMeta(meta);
        return r;
    }

    public ThermalBeams(RogerPlugin plugin) {
        super(plugin, ThermalBeams.id, e -> (BlockEvent.class.isAssignableFrom(e.getClass()) && ((BlockEvent)e).getBlock().getType().equals(ThermalBeams.thermalBeamItem.getType())
                && (!BlockPlaceEvent.class.isAssignableFrom(e.getClass()) || VersionController.get().sameItem(((BlockPlaceEvent)e).getItemInHand(), ThermalBeams.thermalBeamItem))), false, true, new StoreThermalBeam());
    }

    @Override
    public @NotNull ThermalBeam onCustomBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Vector facing = new Vector(1,0,0);
        try {
            facing = DirectionGetter.getDirection(blockPlaceEvent.getBlock()).getFacingVector();
        } catch (IllegalArgumentException ignore) {}
        return new ThermalBeam(blockPlaceEvent.getBlock().getLocation(), facing);
    }

    @Override
    public boolean onCustomBlockBreak(BlockBreakEvent blockBreakEvent, ThermalBeam thermalBeam) {
        return false;
    }

    public void playAllParticles() {
        this.getAllBlocks(e -> {
            if (e.getValue().getChunk().isLoaded()) e.getKey().playParticles();
        });
    }


    private static ThermalBeams instance = null;
    public static ThermalBeams setInstance(ThermalBeams instance) {
        ThermalBeams.instance = instance;
        return instance;
    }

    public static ThermalBeams getInstance() {
        return ThermalBeams.instance;
    }
}

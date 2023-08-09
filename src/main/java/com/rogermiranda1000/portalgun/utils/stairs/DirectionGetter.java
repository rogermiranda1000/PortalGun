package com.rogermiranda1000.portalgun.utils.stairs;

import com.rogermiranda1000.versioncontroller.Version;
import com.rogermiranda1000.versioncontroller.VersionController;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.util.Vector;

public class DirectionGetter {
    public enum Direction {
        EAST(1,0),
        WEST(-1,0),
        SOUTH(0,1),
        NORTH(0,-1);

        private final Vector facing;
        private Direction(Vector facing) {
            this.facing = facing;
        }

        private Direction(float x, float y, float z) {
            this(new Vector(x,y,z));
        }

        private Direction(float x, float z) {
            this(new Vector(x, 0, z));
        }

        public Vector getFacingVector() {
            return this.facing;
        }
    }

    public static DirectionGetter.Direction getDirection(Block block) throws IllegalArgumentException {
        if (VersionController.version.compareTo(Version.MC_1_13) >= 0) {
            BlockData bd = block.getBlockData();
            if (!Stairs.class.isAssignableFrom(bd.getClass())) throw new IllegalArgumentException("Block must be a stair");
            Stairs stair = (Stairs) bd;
            return DirectionGetter.Direction.valueOf(stair.getFacing().name());
        }
        else {
            // pre-flattening
            // @ref https://github.com/PrismarineJS/minecraft-data/blob/master/data/pc/common/legacy.json#L1059
            if (!block.getType().equals(Material.QUARTZ_STAIRS)) throw new IllegalArgumentException("Block must be a quartz stair");
            byte sub_id = block.getData();
            return DirectionGetter.Direction.values()[sub_id % 4];
        }
    }
}

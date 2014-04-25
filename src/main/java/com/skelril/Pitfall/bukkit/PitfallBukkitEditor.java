package com.skelril.Pitfall.bukkit;

import com.skelril.Pitfall.DataPair;
import com.skelril.Pitfall.PitfallEditor;
import com.skelril.Pitfall.Point;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.LinkedList;
import java.util.List;

public class PitfallBukkitEditor extends PitfallEditor<World, DataPair<Material, Byte>> {

    private List<BlockState> oldStates = new LinkedList<BlockState>();

    public PitfallBukkitEditor(World world) {
        super(world);
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getMaxY() {
        return getWorld().getMaxHeight();
    }

    @Override
    public boolean edit(Point pt, DataPair<Material, Byte> type) {
        Block block = getWorld().getBlockAt(pt.getX(), pt.getY(), pt.getZ());
        oldStates.add(block.getState());
        return block.setTypeIdAndData(type.getType().getId(), type.getData(), true);
    }

    @Override
    public DataPair<Material, Byte> getAt(Point pt) {
        Block block = getWorld().getBlockAt(pt.getX(), pt.getY(), pt.getZ());
        return new DataPair<Material, Byte>(block.getType(), block.getData());
    }

    @Override
    public void revertAll() {
        for (BlockState state : oldStates) {
            state.update(true);
        }
    }
}

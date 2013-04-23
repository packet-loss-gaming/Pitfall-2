package com.skelril.Pitfall;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public abstract class PitfallWorker implements Runnable {

    protected int maxRadius = 3;
    protected int destructiveHeight = 1;
    protected BaseBlock targetBlock = new BaseBlock(BlockID.CLAY);
    protected Set<BaseBlock> blackListedBlocks = new HashSet<BaseBlock>();

    public abstract void revertAll();

    public void setMaxRadius(int radius) {

        this.maxRadius = radius;
    }

    public void setDestructiveHeight(int destructiveHeight) {

        this.destructiveHeight = destructiveHeight;
    }

    public void setBaseBlock(BaseBlock targetedBlock) {

        this.targetBlock = targetedBlock;
    }

    public Set<BaseBlock> getBlackList() {

        return blackListedBlocks;
    }

    public EditSession edit(LocalWorld world, Vector origin) throws MaxChangedBlocksException {

        EditSession editor = new EditSession(world, -1);
        trigger(editor, origin);
        return editor;
    }

    public int trigger(EditSession boundSession, Vector origin) throws MaxChangedBlocksException {

        int affected = 0;
        int originX = origin.getBlockX();
        int originY = origin.getBlockY();
        int originZ = origin.getBlockZ();

        HashSet<BlockVector> visited = new HashSet<BlockVector>();
        Stack<BlockVector> queue = new Stack<BlockVector>();

        queue.push(new BlockVector(originX, originY, originZ));

        while (!queue.empty()) {
            BlockVector pt = queue.pop();
            int cx = pt.getBlockX();
            int cy = pt.getBlockY();
            int cz = pt.getBlockZ();

            if (visited.contains(pt)) continue;
            visited.add(pt);

            double dist = Math.max(Math.abs(cx - originX), Math.abs(cz - originZ));

            if (dist > maxRadius) {
                continue;
            }

            if (boundSession.getBlock(pt).equals(targetBlock)) {
                if (blackListedBlocks.contains(boundSession.getBlock(pt.add(0, 1, 0)))) continue;
                affected+=triggerVert(boundSession, pt);
            } else {
                continue;
            }

            queue.push(new BlockVector(cx + 1, cy, cz));
            queue.push(new BlockVector(cx - 1, cy, cz));
            queue.push(new BlockVector(cx, cy, cz + 1));
            queue.push(new BlockVector(cx, cy, cz - 1));
        }

        return affected;
    }

    public int triggerVert(EditSession boundSession, BlockVector origin) throws MaxChangedBlocksException {

        int affected = 0;
        int originX = origin.getBlockX();
        int originY = origin.getBlockY();
        int originZ = origin.getBlockZ();

        Stack<BlockVector> queue = new Stack<BlockVector>();

        queue.push(new BlockVector(originX, originY, originZ));
        while (!queue.isEmpty()) {
            BlockVector pt = queue.pop();
            int cx = pt.getBlockX();
            int cy = pt.getBlockY();
            int cz = pt.getBlockZ();

            if (cy < 0 || cy < originY || cy > originY + destructiveHeight || cy > boundSession.getWorld().getMaxY()) {
                continue;
            }

            if (!blackListedBlocks.contains(boundSession.getBlock(pt)) || cy == originY) {
                if (boundSession.setBlock(pt, new BaseBlock(BlockID.AIR))) affected++;
            } else {
                continue;
            }

            queue.push(new BlockVector(cx, cy + 1, cz));
        }

        return affected;
    }
}

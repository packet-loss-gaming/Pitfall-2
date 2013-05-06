package com.skelril.Pitfall;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.blocks.BaseBlock;

public interface PitfallBlockChangeEvent {

    public BlockWorldVector getLocation();

    public BaseBlock getFrom();
    public BaseBlock getTo();

    public void setTo(BaseBlock to);

    public boolean wasCancelled();
}

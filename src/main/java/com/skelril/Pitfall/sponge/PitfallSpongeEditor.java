/*
 * Copyright (c) 2016 Wyatt Childers.
 *
 * This file is part of Pitfall.
 *
 * Pitfall is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pitfall is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Pitfall.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.skelril.Pitfall.sponge;

import com.skelril.Pitfall.PitfallEditor;
import com.skelril.Pitfall.Point;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class PitfallSpongeEditor extends PitfallEditor<World, BlockType> {

    private List<BlockSnapshot> oldStates = new ArrayList<>();

    public PitfallSpongeEditor(World world) {
        super(world);
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getMaxY() {
        return 255;
    }

    @Override
    public boolean edit(Point pt, BlockType blockType) {
        oldStates.add(getWorld().createSnapshot(pt.getX(), pt.getY(), pt.getZ()));
        getWorld().setBlockType(
                pt.getX(),
                pt.getY(),
                pt.getZ(),
                blockType,
                true,
                Cause.source(PitfallPlugin.container()).build()
        );
        return true;
    }

    @Override
    public BlockType getAt(Point pt) {
        return getWorld().getBlockType(pt.getX(), pt.getY(), pt.getZ());
    }

    @Override
    public void revertAll() {
        for (BlockSnapshot snapshot : oldStates) {
            snapshot.restore(true, true);
        }
    }
}

/*
 * Copyright (c) 2014 Wyatt Childers.
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
 */

package com.skelril.Pitfall;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public abstract class PitfallWorker<World, Type extends DataPair<?, ?>> implements Runnable {

    protected int maxRadius = 3;
    protected int destructiveHeight = 1;
    protected Type targetBlock;
    protected Set<Type> blackListedBlocks = new HashSet<Type>();

    public abstract void revertAll();

    public abstract void activateItemCheck(boolean enable);
    public abstract void activateCreatureCheck(boolean enable);

    public void setMaxRadius(int radius) {
        this.maxRadius = radius;
    }

    public void setDestructiveHeight(int destructiveHeight) {
        this.destructiveHeight = destructiveHeight;
    }

    public void setTargetBlock(Type targetedBlock) {
        this.targetBlock = targetedBlock;
    }

    public abstract boolean checkBlackList(Type type);

    public Set<Type> getBlackList() {
        return blackListedBlocks;
    }

    public int trigger(PitfallEditor<World, Type> editor, Point origin) {

        int affected = 0;
        final int originX = origin.getX();
        final int originZ = origin.getZ();

        HashSet<Point> visited = new HashSet<Point>();
        Stack<Point> queue = new Stack<Point>();

        queue.push(origin.clone());

        while (!queue.empty()) {
            Point pt = queue.pop();
            final int cx = pt.getX();
            final int cz = pt.getZ();

            if (visited.contains(pt)) continue;
            visited.add(pt);

            double dist = Math.max(Math.abs(cx - originX), Math.abs(cz - originZ));

            if (dist > maxRadius) {
                continue;
            }

            if (targetBlock.equals(editor.getAt(pt))) {
                Type above = editor.getAt(pt.withY(pt.getY() + 1));
                if (checkBlackList(above)) continue;
                affected += triggerVert(editor, pt);
            } else {
                continue;
            }

            queue.push(pt.withX(cx + 1));
            queue.push(pt.withX(cx - 1));
            queue.push(pt.withZ(cz + 1));
            queue.push(pt.withZ(cz - 1));
        }

        return affected;
    }

    public int triggerVert(PitfallEditor<World, Type> editor, Point origin) {

        int affected = 0;
        final int originY = origin.getY();

        Stack<Point> queue = new Stack<Point>();

        queue.push(origin.clone());
        while (!queue.isEmpty()) {
            Point pt = queue.pop();
            final int cy = pt.getY();

            if (cy < editor.getMinY() || cy < originY || cy > originY + destructiveHeight || cy > editor.getMaxY()) {
                continue;
            }

            Type above = editor.getAt(pt);
            if (!checkBlackList(above) || cy == originY) {
                PitfallBlockChange<Type> event = callEvent(editor.getWorld(), pt);
                if (event.isAllowed()) {
                    editor.edit(event.getTargetPoint(), event.getNewType());
                    affected++;
                }
            } else {
                continue;
            }

            queue.push(pt.withY(cy + 1));
        }

        return affected;
    }

    protected abstract PitfallBlockChange<Type> callEvent(World world, Point pt);
}

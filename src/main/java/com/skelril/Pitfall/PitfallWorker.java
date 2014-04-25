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
                if (blackListedBlocks.contains(above)) continue;
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
            if (!blackListedBlocks.contains(above) || cy == originY) {
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

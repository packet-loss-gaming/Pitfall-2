package com.skelril.Pitfall;

public abstract class PitfallEditor<World, Type extends DataPair<?, ?>> {

    private World world;

    public PitfallEditor(World world) {
        this.world = world;
    }

    public abstract int getMinY();
    public abstract int getMaxY();

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public abstract boolean edit(Point pt, Type type);
    public abstract Type getAt(Point pt);
    public abstract void revertAll();
}

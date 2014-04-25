package com.skelril.Pitfall;

public interface PitfallBlockChange<Type extends DataPair<?, ?>> {
    public Type getNewType();
    public Point getTargetPoint();
    public boolean isAllowed();
}

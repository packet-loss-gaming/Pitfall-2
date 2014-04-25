package com.skelril.Pitfall.bukkit.event;

import com.skelril.Pitfall.DataPair;
import com.skelril.Pitfall.PitfallBlockChange;
import com.skelril.Pitfall.Point;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PitfallBlockChangeEvent extends Event implements Cancellable,
        PitfallBlockChange<DataPair<Material, Byte>> {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Location target;
    private DataPair<Material, Byte> newType;

    public PitfallBlockChangeEvent(Location target) {
        this.target = target;
    }

    public Location getLocation() {
        return target;
    }

    public void setLocation(Location target) {
        Validate.notNull(target);
        Validate.isTrue(this.target.getWorld().equals(target.getWorld()));
        this.target = target;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public DataPair<Material, Byte> getNewType() {
        return newType;
    }

    public void setNewType(DataPair<Material, Byte> newType) {
        this.newType = newType;
    }

    @Override
    public Point getTargetPoint() {
        return new Point(target.getBlockX(), target.getBlockY(), target.getBlockZ());
    }

    @Override
    public boolean isAllowed() {
        return cancelled;
    }
}

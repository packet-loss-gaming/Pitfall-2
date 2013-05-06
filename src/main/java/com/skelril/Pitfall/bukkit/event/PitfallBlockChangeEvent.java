package com.skelril.Pitfall.bukkit.event;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PitfallBlockChangeEvent extends Event implements com.skelril.Pitfall.PitfallBlockChangeEvent, Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final BlockWorldVector location;
    private final BaseBlock from;
    private BaseBlock to;

    public PitfallBlockChangeEvent(BlockWorldVector location, BaseBlock from, BaseBlock to) {

        this.location = location;
        this.from = from;
        this.to = to;
    }

    @Override
    public BlockWorldVector getLocation() {

        return location;
    }

    @Override
    public BaseBlock getFrom() {

        return from;
    }

    @Override
    public BaseBlock getTo() {

        return to;
    }

    @Override
    public void setTo(BaseBlock to) {

        this.to = to;
    }

    @Override
    public boolean wasCancelled() {

        return cancelled;
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
}

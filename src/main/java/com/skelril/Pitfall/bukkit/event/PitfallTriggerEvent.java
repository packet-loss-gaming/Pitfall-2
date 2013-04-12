package com.skelril.Pitfall.bukkit.event;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class PitfallTriggerEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Block block;
    private int triggerDelay;
    private int returnDelay;

    public PitfallTriggerEvent(Entity entity, Block block, int triggerDelay, int returnDelay) {
        super(entity);
        this.block = block;
        this.triggerDelay = triggerDelay;
        this.returnDelay = returnDelay;
    }

    public Block getBlock() {

        return block;
    }

    public int getTriggerDelay() {

        return triggerDelay;
    }

    public void setTriggerDelay(int triggerDelay) {

        Validate.isTrue(triggerDelay > 0, "Trigger delay cannot be less than 1 tick.");

        this.triggerDelay = triggerDelay;
    }

    public int getReturnDelay() {

        return returnDelay;
    }

    public void setReturnDelay(int returnDelay) {

        Validate.isTrue(returnDelay > 0, "Return delay cannot be less than 1 tick.");

        this.returnDelay = returnDelay;
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
}

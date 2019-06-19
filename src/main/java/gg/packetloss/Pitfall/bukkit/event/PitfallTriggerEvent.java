/*
 * Copyright (c) 2019 Wyatt Childers.
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

package gg.packetloss.Pitfall.bukkit.event;

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

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

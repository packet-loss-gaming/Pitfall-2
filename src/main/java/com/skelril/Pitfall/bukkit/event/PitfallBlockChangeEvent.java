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

package com.skelril.Pitfall.bukkit.event;

import com.skelril.Pitfall.DataPair;
import com.skelril.Pitfall.PitfallBlockChange;
import com.skelril.Pitfall.Point;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PitfallBlockChangeEvent extends Event implements Cancellable,
        PitfallBlockChange<DataPair<Material, Byte>> {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Location target;
    private DataPair<Material, Byte> newType = new DataPair<Material, Byte>(Material.AIR, (byte) 0);

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
        return !cancelled;
    }
}

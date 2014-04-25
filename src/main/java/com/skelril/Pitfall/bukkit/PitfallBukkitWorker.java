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

package com.skelril.Pitfall.bukkit;

import com.skelril.Pitfall.DataPair;
import com.skelril.Pitfall.PitfallBlockChange;
import com.skelril.Pitfall.PitfallWorker;
import com.skelril.Pitfall.Point;
import com.skelril.Pitfall.bukkit.event.PitfallBlockChangeEvent;
import com.skelril.Pitfall.bukkit.event.PitfallTriggerEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

public class PitfallBukkitWorker extends PitfallWorker<World, DataPair<Material, Byte>> {

    private static final PitfallPlugin plugin = PitfallPlugin.inst();
    private static final Server server = plugin.getServer();
    private static final Logger log = plugin.getLogger();

    private Set<PitfallBukkitEditor> records = new CopyOnWriteArraySet<PitfallBukkitEditor>();
    private Set<Class> targeted = new HashSet<Class>();
    private int defaultTrapDelay = 2;
    private int defaultReturnDelay = 60;

    public PitfallBukkitWorker() {
        targeted.add(Player.class);
        blackListedBlocks.add(new DataPair<Material, Byte>(Material.AIR, (byte) 0));
    }

    @Override
    public void revertAll() {
        for (PitfallBukkitEditor record : records) {
            record.revertAll();
        }
        records.clear();
    }

    @Override
    public void run() {
        for (final World world : plugin.getServer().getWorlds()) {
            for (final Entity entity : world.getEntitiesByClasses(targeted.toArray(new Class[targeted.size()]))) {

                // Perform some checks to see if we should precede
                if (entity instanceof Player && !((Player) entity).hasPermission("pitfall.trigger")) continue;

                final Block h = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
                final Block b = h.getRelative(BlockFace.DOWN);

                DataPair<Material, Byte> hPair = new DataPair<Material, Byte>(h.getType(), h.getData());
                DataPair<Material, Byte> bPair = new DataPair<Material, Byte>(b.getType(), b.getData());

                if (targetBlock.equals(bPair) && !checkBlackList(hPair)) {

                    PitfallTriggerEvent event = new PitfallTriggerEvent(entity, b, defaultTrapDelay, defaultReturnDelay);
                    server.getPluginManager().callEvent(event);
                    final PitfallTriggerEvent finalEvent = event;
                    if (finalEvent.isCancelled()) continue;

                    server.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            final PitfallBukkitEditor record = new PitfallBukkitEditor(world);

                            Block b = finalEvent.getBlock();
                            trigger(record, new Point(b.getX(), b.getY(), b.getZ()));
                            records.add(record);
                            server.getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if (!records.contains(record)) return;
                                    record.revertAll();
                                    records.remove(record);
                                }
                            }, finalEvent.getReturnDelay());
                        }
                    }, finalEvent.getTriggerDelay());
                }
            }
        }
    }

    @Override
    public void activateItemCheck(boolean enable) {
        if (enable) {
            targeted.add(Item.class);
        } else {
            targeted.remove(Item.class);
        }
    }

    @Override
    public void activateCreatureCheck(boolean enable) {
        if (enable) {
            targeted.add(Creature.class);
        } else {
            targeted.remove(Creature.class);
        }
    }

    @Override
    public boolean checkBlackList(DataPair<Material, Byte> type) {
        return blackListedBlocks.contains(type) || blackListedBlocks.contains(type.withData((byte) -1));
    }

    @Override
    protected PitfallBlockChange<DataPair<Material, Byte>> callEvent(World world, Point pt) {
        return new PitfallBlockChangeEvent(new Location(world, pt.getX(), pt.getY(), pt.getZ()));
    }

    public void setDefaultTrapDelay(int delay) {
        this.defaultTrapDelay = delay;
    }
    public void setDefaultReturnDelay(int delay) {
        this.defaultReturnDelay = delay;
    }
}

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

package gg.packetloss.Pitfall.bukkit;

import gg.packetloss.Pitfall.PitfallBlockChange;
import gg.packetloss.Pitfall.PitfallWorker;
import gg.packetloss.Pitfall.Point;
import gg.packetloss.Pitfall.bukkit.event.PitfallBlockChangeEvent;
import gg.packetloss.Pitfall.bukkit.event.PitfallTriggerEvent;
import org.bukkit.*;
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

public class PitfallBukkitWorker extends PitfallWorker<World, Material> {

    private static final PitfallPlugin plugin = PitfallPlugin.inst();
    private static final Server server = plugin.getServer();
    private static final Logger log = plugin.getLogger();

    private Set<PitfallBukkitEditor> records = new CopyOnWriteArraySet<PitfallBukkitEditor>();
    private Set<Class> targeted = new HashSet<Class>();
    private int defaultTrapDelay = 2;
    private int defaultReturnDelay = 60;

    public PitfallBukkitWorker() {
        targeted.add(Player.class);
        blackListedBlocks.add(Material.AIR);
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
            for (final Entity entity : world.getEntitiesByClasses(targeted.toArray(new Class[0]))) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (ignoreSpectators && player.getGameMode() == GameMode.SPECTATOR) {
                        continue;
                    }

                    if (checkPermissions && !player.hasPermission("pitfall.trigger")) {
                        continue;
                    }
                }

                final Block h = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
                final Block b = h.getRelative(BlockFace.DOWN);

                Material hMat = h.getType();
                Material bMat = b.getType();

                if (targetBlock.equals(bMat) && !checkBlackList(hMat)) {

                    PitfallTriggerEvent event = new PitfallTriggerEvent(entity, b, defaultTrapDelay, defaultReturnDelay);
                    server.getPluginManager().callEvent(event);
                    final PitfallTriggerEvent finalEvent = event;
                    if (finalEvent.isCancelled()) continue;

                    server.getScheduler().runTaskLater(plugin, () -> {
                        final PitfallBukkitEditor record = new PitfallBukkitEditor(world);

                        Block b1 = finalEvent.getBlock();
                        trigger(record, new Point(b1.getX(), b1.getY(), b1.getZ()));
                        records.add(record);
                        server.getScheduler().runTaskLater(plugin, () -> {
                            if (!records.contains(record)) return;
                            record.revertAll();
                            records.remove(record);
                        }, finalEvent.getReturnDelay());
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
    public boolean checkBlackList(Material type) {
        return blackListedBlocks.contains(type);
    }

    @Override
    protected PitfallBlockChange<Material> callEvent(World world, Point pt) {
        return new PitfallBlockChangeEvent(new Location(world, pt.getX(), pt.getY(), pt.getZ()));
    }

    public void setDefaultTrapDelay(int delay) {
        this.defaultTrapDelay = delay;
    }
    public void setDefaultReturnDelay(int delay) {
        this.defaultReturnDelay = delay;
    }
}

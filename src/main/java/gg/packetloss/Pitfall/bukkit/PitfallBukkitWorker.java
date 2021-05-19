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
import org.bukkit.entity.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class PitfallBukkitWorker extends PitfallWorker<World, Material> {

    private static final PitfallPlugin plugin = PitfallPlugin.inst();
    private static final Server server = plugin.getServer();
    private static final Logger log = plugin.getLogger();

    private Set<PitfallBukkitEditor> records = new CopyOnWriteArraySet<>();
    private Set<Class<? extends Entity>> targeted = new HashSet<>();
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

    private boolean checkFromPosition(Location location, boolean includeAir) {
        location = location.clone();
        final Block h = location.add(0, -1, 0).getBlock();
        final Block b = location.add(0, -1, 0).getBlock();

        Material hMat = h.getType();
        Material bMat = b.getType();

        if (includeAir && bMat.isAir() && hMat.isAir()) {
            return true;
        }

        return targetBlock.equals(bMat) && !checkBlackList(hMat);
    }

    private void activateAtBlock(Entity entity, Location location) {
        Block triggeringBlock = location.clone().add(0, -2, 0).getBlock();
        PitfallTriggerEvent event = new PitfallTriggerEvent(
                entity,
                triggeringBlock,
                defaultTrapDelay,
                defaultReturnDelay
        );

        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        server.getScheduler().runTaskLater(plugin, () -> {
            final PitfallBukkitEditor record = new PitfallBukkitEditor(location.getWorld());

            Block b1 = event.getBlock();
            trigger(record, new Point(b1.getX(), b1.getY(), b1.getZ()));
            records.add(record);
            server.getScheduler().runTaskLater(plugin, () -> {
                if (!records.contains(record)) return;
                record.revertAll();
                records.remove(record);
            }, event.getReturnDelay());
        }, event.getTriggerDelay());
    }

    private boolean isChunkLoaded(Location location) {
        return location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    private boolean isApproximately(double value, double target) {
        return target - 0.2 < value && value < target + 0.2;
    }

    private boolean isWeakPitfall(Location location) {
        if (!isChunkLoaded(location)) {
            return false;
        }

        return checkFromPosition(location, true);
    }

    private void getCheckLocations(Entity entity, Predicate<Location> consumer) {
        Location location = entity.getLocation();

        double relativeX = Math.abs(location.getX() - location.getBlockX());
        double relativeZ = Math.abs(location.getZ() - location.getBlockZ());

        boolean roughXMatch = isApproximately(relativeX, .5);
        boolean roughZMatch = isApproximately(relativeZ, .5);

        boolean isCurrentAWeakPitfall = isWeakPitfall(location.clone());
        if (!isCurrentAWeakPitfall) {
            return;
        }

        if (roughXMatch && roughZMatch) {
            consumer.test(location.clone());
        } else if (roughXMatch) {
            // We're roughly in the middle x wise, check to see if the nearest z block
            // looks weak.
            if (isWeakPitfall(location.clone().add(0, 0, relativeZ < 0.5 ? -1 : 1))) {
                consumer.test(location.clone());
            }
        } else if (roughZMatch) {
            // We're roughly in the middle z wise, check to see if the nearest x block
            // looks weak.
            if (isWeakPitfall(location.clone().add(relativeX < 0.5 ? -1 : 1, 0, 0))) {
                consumer.test(location.clone());
            }
        } else {
            // We're in between 4 blocks, check the remaining blocks
            if (isWeakPitfall(location.clone().add(relativeX < 0.5 ? -1 : 1, 0, 0)) &&
                isWeakPitfall(location.clone().add(0, 0, relativeZ < 0.5 ? -1 : 1)) &&
                isWeakPitfall(location.clone().add(relativeX < 0.5 ? -1 : 1, 0, relativeZ < 0.5 ? -1 : 1))) {
                consumer.test(location.clone());
            }
        }
    }

    @Override
    public void run() {
        for (final World world : plugin.getServer().getWorlds()) {
            for (final Entity entity : world.getEntitiesByClasses(targeted.toArray(new Class[0]))) {
                if (entity instanceof WaterMob || entity instanceof Flying) {
                    continue;
                }

                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (ignoreSpectators && player.getGameMode() == GameMode.SPECTATOR) {
                        continue;
                    }

                    if (player.isSwimming()) {
                        continue;
                    }

                    if (checkPermissions && !player.hasPermission("pitfall.trigger")) {
                        continue;
                    }
                }

                // Skip entities that are still falling
                double entityY = entity.getLocation().getY();
                if (entityY != Math.floor(entityY)) {
                    continue;
                }

                getCheckLocations(entity, (testLoc) -> {
                    if (checkFromPosition(testLoc, false)) {
                        activateAtBlock(entity, testLoc);
                        return true;
                    }

                    return false;
                });
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
            targeted.add(LivingEntity.class);
        } else {
            targeted.remove(LivingEntity.class);
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

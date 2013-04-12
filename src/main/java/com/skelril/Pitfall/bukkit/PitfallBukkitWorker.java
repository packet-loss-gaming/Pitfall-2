package com.skelril.Pitfall.bukkit;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.skelril.Pitfall.PitfallWorker;
import com.skelril.Pitfall.bukkit.PitfallPlugin;
import com.skelril.Pitfall.bukkit.event.PitfallTriggerEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class PitfallBukkitWorker extends PitfallWorker {

    private static final PitfallPlugin plugin = PitfallPlugin.inst();
    private static final Server server = plugin.getServer();
    private static final Logger log = plugin.getLogger();


    private Set<Class> targeted = new HashSet<Class>();
    private int defaultTrapDelay = 2;
    private int defaultReturnDelay = 60;

    public PitfallBukkitWorker() {

        targeted.add(Player.class);
        blackListedBlocks.add(new BaseBlock(BlockID.AIR));
    }

    @Override
    public void run() {

        for (final World world : plugin.getServer().getWorlds()) {
            for (final Entity entity : world.getEntitiesByClasses(targeted.toArray(new Class[targeted.size()]))) {

                // Perform some checks to see if we should precede
                if (entity instanceof Player && !((Player) entity).hasPermission("pitfall.trigger")) continue;

                final Block h = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
                final Block b = h.getRelative(BlockFace.DOWN);

                final BaseBlock hBase = new BaseBlock(h.getTypeId(), h.getData());
                final BaseBlock bBase = new BaseBlock(b.getTypeId(), b.getData());

                if (bBase.equals(targetBlock) && !blackListedBlocks.contains(hBase)) {

                    PitfallTriggerEvent event = new PitfallTriggerEvent(entity, b, defaultTrapDelay, defaultReturnDelay);
                    server.getPluginManager().callEvent(event);
                    final PitfallTriggerEvent finalEvent = event;
                    if (finalEvent.isCancelled()) continue;

                    server.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final EditSession editor = edit(new BukkitWorld(world), BukkitUtil.toVector(b.getLocation()));
                                server.getScheduler().runTaskLater(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        editor.undo(editor);
                                    }
                                }, finalEvent.getReturnDelay());
                            } catch (MaxChangedBlocksException ignored) {
                                // This is currently not limited, so there will be no issue
                            }
                        }
                    }, finalEvent.getTriggerDelay());
                }
            }
        }
    }

    public void activateItemCheck(boolean enable) {

        if (targeted.contains(Item.class) && !enable) {
            targeted.remove(Item.class);
        } else if (enable) {
            targeted.add(Item.class);
        }
    }

    public void activateCreatureCheck(boolean enable) {

        if (targeted.contains(Creature.class) && !enable) {
            targeted.remove(Creature.class);
        } else if (enable) {
            targeted.add(Creature.class);
        }
    }

    public void setDefaultTrapDelay(int delay) {

        this.defaultTrapDelay = delay;
    }
    public void setDefaultReturnDelay(int delay) {

        this.defaultReturnDelay = delay;
    }
}

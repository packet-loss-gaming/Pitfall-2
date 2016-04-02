package com.skelril.Pitfall.sponge;

import com.skelril.Pitfall.PitfallBlockChange;
import com.skelril.Pitfall.PitfallWorker;
import com.skelril.Pitfall.Point;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class PitfallSpongeWorker extends PitfallWorker<World, BlockType, GameMode> {

    private List<PitfallSpongeEditor> records = new ArrayList<>();
    private Set<Class> targeted = new HashSet<>();
    private int defaultTrapDelay = 2;
    private int defaultReturnDelay = 60;

    public PitfallSpongeWorker() {
        targeted.add(Player.class);
        blackListedBlocks.add(BlockTypes.AIR);
    }

    @Override
    public void revertAll() {
        for (PitfallSpongeEditor record : records) {
            record.revertAll();
        }
        records.clear();
    }

    @Override
    public void run() {
        for (final World world : Sponge.getServer().getWorlds()) {
            Collection<Entity> entities = world.getEntities(a -> {
                for (Class clazz : targeted) {
                    if (clazz.isInstance(a)) {
                        return true;
                    }
                }
                return false;
            });
            for (final Entity entity : entities) {

                // Perform some checks to see if we should precede
                if (entity instanceof Player && !((Player) entity).hasPermission("pitfall.trigger")) continue;

                Optional<GameMode> optGameMode = entity.get(Keys.GAME_MODE);
                if (optGameMode.isPresent() && ignoredGameModes.contains(optGameMode.get())) {
                    continue;
                }

                final PitfallSpongeEditor editor = new PitfallSpongeEditor(world);

                Location<World> eLoc = entity.getLocation();
                if (editor.getMaxY() < eLoc.getY() || editor.getMinY() > eLoc.getY() - 2) {
                    continue;
                }

                Location<World> hLoc = eLoc.add(0, -1, 0);
                Location<World> bLoc = hLoc.add(0, -1, 0);

                final BlockType h = hLoc.getBlockType();
                final BlockType b = bLoc.getBlockType();

                if (targetBlock.equals(b) && !checkBlackList(h)) {
                    Task.builder().execute(() -> {

                        trigger(editor, new Point(bLoc.getBlockX(), bLoc.getBlockY(), bLoc.getBlockZ()));
                        records.add(editor);
                        Task.builder().execute(() -> {
                            if (!records.contains(editor)) return;
                            editor.revertAll();
                            records.remove(editor);
                        }).delayTicks(defaultReturnDelay).submit(PitfallPlugin.inst());
                    }).delayTicks(defaultTrapDelay).submit(PitfallPlugin.inst());
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
    public boolean checkBlackList(BlockType type) {
        return blackListedBlocks.contains(type);
    }

    @Override
    protected PitfallBlockChange<BlockType> callEvent(World world, Point pt) {
        return new PitfallBlockChange<BlockType>() {

            @Override
            public BlockType getNewType() {
                return BlockTypes.AIR;
            }

            @Override
            public Point getTargetPoint() {
                return pt;
            }

            @Override
            public boolean isAllowed() {
                return true;
            }
        };
    }

    public void setDefaultTrapDelay(int delay) {
        this.defaultTrapDelay = delay;
    }
    public void setDefaultReturnDelay(int delay) {
        this.defaultReturnDelay = delay;
    }
}
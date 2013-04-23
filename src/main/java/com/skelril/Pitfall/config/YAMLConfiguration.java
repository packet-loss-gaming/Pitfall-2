package com.skelril.Pitfall.config;

import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.blocks.BlockID;
import com.skelril.Pitfall.LocalConfiguration;

import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * A implementation of YAML based off of {@link com.sk89q.worldedit.util.YAMLConfiguration} for PitfallPlugin.
 */
public class YAMLConfiguration extends LocalConfiguration {

    public final YAMLProcessor config;
    protected final Logger logger;
    private FileHandler logFileHandler;

    public YAMLConfiguration(YAMLProcessor config, Logger logger) {

        this.config = config;
        this.logger = logger;
    }

    @Override
    public void load() {

        // Target Block
        pitItem = config.getInt("target-block.id", BlockID.CLAY);
        pitItemData = config.getInt("target-block.data", 0);

        // Black List
        useBlackList = config.getBoolean("blacklist.enable", true);
        ignorePassable = config.getBoolean("blacklist.ignore-passible", true);
        blackListedBlocks = config.getIntList("blacklist.blacklisted-blocks", Arrays.asList(
                BlockID.CHEST, BlockID.TRAPPED_CHEST
        ));

        // Trap Settings
        maxRadius = config.getInt("limits.max-radius", 5);
        destrutiveHeight = config.getInt("limits.destructive-height", 1);
        trapDelay = config.getInt("trap-delay", 2);
        returnDelay = config.getInt("return-delay", 60);
        enableItemTrap = config.getBoolean("enable-item-detection", true);
        enableMonsterTrap = config.getBoolean("enable-monster-detection", true);

        // Save any added values
        config.save();
    }

    public void unload() {

        if (logFileHandler != null) {
            logFileHandler.close();
        }
    }
}

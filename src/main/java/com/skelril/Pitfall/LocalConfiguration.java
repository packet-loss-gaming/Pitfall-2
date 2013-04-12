package com.skelril.Pitfall;

import java.io.File;
import java.util.List;

/**
 * A implementation of Configuration based off of {@link com.sk89q.worldedit.LocalConfiguration} for PitfallPlugin.
 */
public abstract class LocalConfiguration {

    // Target Block
    public int pitItem;
    public int pitItemData;

    // Black List
    public boolean useBlackList;
    public boolean ignorePassable;
    public List<Integer> blackListedBlocks;

    // Trap Settings
    public int maxRadius;
    public int destrutiveHeight;
    public int trapDelay;
    public int returnDelay;
    public boolean enableItemTrap;
    public boolean enableMonsterTrap;

    /**
     * Loads the configuration.
     */
    public abstract void load();

    /**
     * Get the working directory to work from.
     *
     * @return
     */
    public File getWorkingDirectory() {

        return new File(".");
    }
}
package com.skelril.Pitfall.bukkit;

import com.sk89q.util.yaml.YAMLProcessor;
import com.skelril.Pitfall.config.YAMLConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * A implementation of {@link com.sk89q.worldedit.bukkit.BukkitConfiguration} for Pitfall.
 */
public class BukkitConfiguration extends YAMLConfiguration {

    private static final PitfallPlugin plugin = PitfallPlugin.inst();

    public BukkitConfiguration(YAMLProcessor config) {

        super(config, plugin.getLogger());
    }

    @Override
    public void load() {

        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.load();
    }

    @Override
    public File getWorkingDirectory() {

        return plugin.getDataFolder();
    }
}
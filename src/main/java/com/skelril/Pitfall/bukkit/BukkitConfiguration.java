package com.skelril.Pitfall.bukkit;

import com.sk89q.util.yaml.YAMLProcessor;
import com.skelril.Pitfall.config.YAMLConfiguration;
import org.bukkit.Material;

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

    // Target Block
    public Material targetType;
    public byte targetData;

    @Override
    public void load() {
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Target Block
        targetType = Material.valueOf(config.getString("target-block.name", Material.CLAY.toString()));
        targetData = (byte) config.getInt("target-block.data", 0);

        super.load();
    }

    @Override
    public File getWorkingDirectory() {
        return plugin.getDataFolder();
    }
}
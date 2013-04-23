package com.skelril.Pitfall.bukkit;

import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import com.skelril.Pitfall.LocalConfiguration;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Pitfall Plugin for Bukkit originally written by razorstorm.
 *
 * @author Dark_Arc
 */
public class PitfallPlugin extends JavaPlugin {

    private static PitfallPlugin inst;
    private LocalConfiguration config;
    private PitfallBukkitWorker pitfallBukkitWorker;

    @Override
    public void onLoad() {

        inst = this;
    }

    @Override
    public void onEnable() {

        // Config
        createDefaultConfiguration(new File(getDataFolder(), "config.yml"), "config.yml", false);
        config = new BukkitConfiguration(new YAMLProcessor(new File(getDataFolder(), "config.yml"), true,
                YAMLFormat.EXTENDED));
        config.load();

        // New version printing
        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " enabled.");

        // Setup the Engine
        pitfallBukkitWorker = new PitfallBukkitWorker();
        pitfallBukkitWorker.setDefaultTrapDelay(config.trapDelay);
        pitfallBukkitWorker.setDefaultReturnDelay(config.returnDelay);
        pitfallBukkitWorker.activateItemCheck(config.enableItemTrap);
        pitfallBukkitWorker.activateCreatureCheck(config.enableMonsterTrap);
        pitfallBukkitWorker.setMaxRadius(config.maxRadius);
        pitfallBukkitWorker.setDestructiveHeight(config.destrutiveHeight);
        pitfallBukkitWorker.setBaseBlock(new BaseBlock(config.pitItem, config.pitItemData));

        // Blacklist setup
        if (config.useBlackList) {

            Set<BaseBlock> exceptions = new HashSet<BaseBlock>();
            Set<BaseBlock> blackList = pitfallBukkitWorker.getBlackList();

            for (int typeId : config.blackListedBlocks) {
                if (typeId < 0) {
                    exceptions.add(new BaseBlock(typeId * -1));
                    continue;
                }
                blackList.add(new BaseBlock(typeId));
            }

            if (config.ignorePassable) {
                for (Material material : Material.values()) {
                    if (BlockType.canPassThrough(material.getId())) blackList.add(new BaseBlock(material.getId()));
                }
            }

            blackList.removeAll(exceptions);
        }

        // Start the watcher
        getServer().getScheduler().scheduleSyncRepeatingTask(this, pitfallBukkitWorker, 20 * 3, 5);
    }

    @Override
    public void onDisable() {

        pitfallBukkitWorker.revertAll();
        getLogger().info(getDescription().getName() + " disabled.");
    }

    /**
     * Create a default configuration file from the .jar.
     *
     * @param actual The destination file
     * @param defaultName The name of the file inside the jar's defaults folder
     * @param force If it should make the file even if it already exists
     */
    public void createDefaultConfiguration(File actual, String defaultName, boolean force) {

        // Make parent directories
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (actual.exists() && !force) {
            return;
        }

        InputStream input = null;
        JarFile file = null;
        try {
            file = new JarFile(getFile());
            ZipEntry copy = file.getEntry("defaults/" + defaultName);
            if (copy == null) {
                file.close();
                throw new FileNotFoundException();
            }
            input = file.getInputStream(copy);
        } catch (IOException e) {
            getLogger().severe("Unable to read default configuration: " + defaultName);
        }

        if (input != null) {
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(actual);
                byte[] buf = new byte[8192];
                int length = 0;
                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                if (!force) getLogger().info("Default configuration file written: " + actual.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    file.close();
                } catch (IOException e) {
                }

                try {
                    input.close();
                } catch (IOException ignore) {
                }

                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * You should not use this to access Pitfall from your own plugins.
     *
     * @return the PitfallPlugin instance
     */
    public static PitfallPlugin inst() {

        return inst;
    }

    public LocalConfiguration getLocalConfiguration() {

        return config;
    }

    public PitfallBukkitWorker getPitfallWorker() {

        return pitfallBukkitWorker;
    }
}

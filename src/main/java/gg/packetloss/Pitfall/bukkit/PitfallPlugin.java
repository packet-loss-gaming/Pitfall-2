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

import gg.packetloss.Pitfall.LocalConfiguration;
import gg.packetloss.Pitfall.util.yaml.YAMLFormat;
import gg.packetloss.Pitfall.util.yaml.YAMLProcessor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Pitfall Plugin for Bukkit idea credited to razorstorm.
 *
 * @author Dark_Arc
 */
public class PitfallPlugin extends JavaPlugin {

    private static PitfallPlugin inst;
    private BukkitConfiguration config;
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
        pitfallBukkitWorker.setTargetBlock(config.targetType);
        pitfallBukkitWorker.setCheckForDestination(config.ignoreIfNoDestination);
        pitfallBukkitWorker.setIgnoreSpectators(config.ignoreSpectators);
        pitfallBukkitWorker.setCheckPermissions(config.checkPitfallPermission);

        // Ignorelist setup
        if (config.useIgnoreList) {
            Set<Material> blockIgnorelist = pitfallBukkitWorker.getBlockIgnorelist();
            blockIgnorelist.addAll(config.ignoredBlocks);

            if (config.ignorePassable) {
                for (Material material : Material.values()) {
                    if (material.isBlock() && !material.isSolid()) blockIgnorelist.add(material);
                }
            }
        }

        // Start the watcher
        getServer().getScheduler().scheduleSyncRepeatingTask(
                this,
                pitfallBukkitWorker,
                20 * 3,
                config.checkFrequency
        );
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

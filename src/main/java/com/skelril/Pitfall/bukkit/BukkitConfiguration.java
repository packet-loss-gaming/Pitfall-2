/*
 * Copyright (c) 2014 Wyatt Childers.
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
 */

package com.skelril.Pitfall.bukkit;

import com.skelril.Pitfall.config.YAMLConfiguration;
import com.skelril.Pitfall.util.yaml.YAMLProcessor;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BukkitConfiguration extends YAMLConfiguration {
    private static final PitfallPlugin plugin = PitfallPlugin.inst();

    public BukkitConfiguration(YAMLProcessor config) {
        super(config, plugin.getLogger());
    }

    // Target Block
    public Material targetType;
    public byte targetData;

    // Black List
    public List<String> blackListedBlocks;


    @Override
    public void load() {
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.load();

        // Target Block
        targetType = Material.valueOf(config.getString("target-block.name", Material.CLAY.toString()));
        targetData = (byte) config.getInt("target-block.data", 0);

        blackListedBlocks = config.getStringList("blacklist.blacklisted-blocks", Arrays.asList(
                Material.CHEST + ":-1", Material.TRAPPED_CHEST + ":-1", Material.SIGN_POST + ":-1"
        ));
    }

    @Override
    public File getWorkingDirectory() {
        return plugin.getDataFolder();
    }
}
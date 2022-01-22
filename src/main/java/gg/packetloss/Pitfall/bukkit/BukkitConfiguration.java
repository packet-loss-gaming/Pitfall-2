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

import gg.packetloss.Pitfall.config.YAMLConfiguration;
import gg.packetloss.Pitfall.util.yaml.YAMLProcessor;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BukkitConfiguration extends YAMLConfiguration {
    private static final PitfallPlugin plugin = PitfallPlugin.inst();

    public BukkitConfiguration(YAMLProcessor config) {
        super(config, plugin.getLogger());
    }

    // Target Block
    public Material targetType;

    // Black List
    public List<Material> ignoredBlocks;

    @Override
    public void load() {
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Target Block
        String targetTypeIds = config.getString("target-block.name", Material.CLAY.toString());
        targetType = Material.getMaterial(targetTypeIds);

        List<String> ignoredBlockNames = config.getStringList("ignore.blocks", Arrays.asList(
                Material.CHEST.toString(), Material.TRAPPED_CHEST.toString(), Material.SHULKER_BOX.toString()
        ));
        this.ignoredBlocks = ignoredBlockNames.stream().map(Material::getMaterial).collect(Collectors.toList());

        super.load();
    }

    @Override
    public File getWorkingDirectory() {
        return plugin.getDataFolder();
    }
}
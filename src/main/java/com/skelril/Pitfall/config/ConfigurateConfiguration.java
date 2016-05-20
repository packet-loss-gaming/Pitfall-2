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

package com.skelril.Pitfall.config;

import com.skelril.Pitfall.LocalConfiguration;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;

public class ConfigurateConfiguration extends LocalConfiguration {

    protected final ConfigurationLoader<CommentedConfigurationNode> config;

    protected CommentedConfigurationNode node;

    public ConfigurateConfiguration(ConfigurationLoader<CommentedConfigurationNode> config) {
        this.config = config;
    }

    @Override
    public void load() {
        try {
            ConfigurationOptions options = ConfigurationOptions.defaults();
            options = options.setShouldCopyDefaults(true);

            node = config.load(options);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Black List
        useBlackList = node.getNode("blacklist", "enable").getBoolean(true);
        ignorePassable = node.getNode("blacklist", "ignore-passible").getBoolean(true);

        // Trap Settings
        maxRadius = node.getNode("limits", "max-radius").getInt(5);
        destrutiveHeight = node.getNode("limits", "destructive-height").getInt(1);
        trapDelay = node.getNode("trap-delay").getInt(2);
        returnDelay = node.getNode("return-delay").getInt(60);
        enableItemTrap = node.getNode("enable-item-detection").getBoolean(true);
        enableMonsterTrap = node.getNode("enable-monster-detection").getBoolean(true);
    }
}

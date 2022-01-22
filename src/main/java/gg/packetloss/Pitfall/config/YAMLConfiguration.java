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

package gg.packetloss.Pitfall.config;

import gg.packetloss.Pitfall.LocalConfiguration;
import gg.packetloss.Pitfall.util.yaml.YAMLProcessor;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

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

        // Ignorelist
        useIgnoreList = config.getBoolean("ignore.enable", true);
        ignorePassable = config.getBoolean("ignore.ignore-non-solid-tops", true);
        ignoreIfNoDestination = config.getBoolean("ignore.ignore-if-no-destination", true);
        ignoreSpectators = config.getBoolean("ignore.ignore-spectators", true);

        // Trap Settings
        maxRadius = config.getInt("limits.max-radius", 5);
        destrutiveHeight = config.getInt("limits.destructive-height", 1);
        checkFrequency = config.getInt("check-frequency", 5);
        trapDelay = config.getInt("trap-delay", 2);
        returnDelay = config.getInt("return-delay", 60);
        enableItemTrap = config.getBoolean("enable-item-detection", true);
        enableMonsterTrap = config.getBoolean("enable-monster-detection", true);

        // Permission related setting
        checkPitfallPermission = config.getBoolean("check-pitfall-permission", false);

        // Save any added values
        config.save();
    }

    public void unload() {

        if (logFileHandler != null) {
            logFileHandler.close();
        }
    }
}

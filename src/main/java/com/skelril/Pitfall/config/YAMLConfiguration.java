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
import com.skelril.Pitfall.util.yaml.YAMLProcessor;

public class YAMLConfiguration extends LocalConfiguration {

    public final YAMLProcessor config;

    public YAMLConfiguration(YAMLProcessor config) {
        this.config = config;
    }

    @Override
    public void load() {

        // Black List
        useBlackList = config.getBoolean("blacklist.enable", true);
        ignorePassable = config.getBoolean("blacklist.ignore-passible", true);

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

    public void init() {
        config.writeDefaults();
    }
}

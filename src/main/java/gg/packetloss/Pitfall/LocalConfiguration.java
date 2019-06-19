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

package gg.packetloss.Pitfall;

import java.io.File;

public abstract class LocalConfiguration {

    // Black List
    public boolean useBlackList;
    public boolean ignorePassable;

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
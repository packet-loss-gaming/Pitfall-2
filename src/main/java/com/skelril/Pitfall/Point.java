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

package com.skelril.Pitfall;

public class Point implements Cloneable {
    private int x, y, z;

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point clone() {
        return new Point(x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Point setX(int x) {
        this.x = x;
        return this;
    }

    public Point setY(int y) {
        this.y = y;
        return this;
    }

    public Point setZ(int z) {
        this.z = z;
        return this;
    }

    public Point withX(int x) {
        return new Point(x, y, z);
    }

    public Point withY(int y) {
        return new Point(x, y, z);
    }

    public Point withZ(int z) {
        return new Point(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Point && x == ((Point) o).getX() && y == ((Point) o).getY() && z == ((Point) o).getZ();
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash *= 31 + x;
        hash *= 31 + y;
        hash *= 31 + z;
        return hash;
    }
}

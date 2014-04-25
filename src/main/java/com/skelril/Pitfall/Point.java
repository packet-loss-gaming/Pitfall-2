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
}

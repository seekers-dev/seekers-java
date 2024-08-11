/*
 * Copyright (C) 2022  Seekers Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.seekers.core;

public class Vector2D {
    public static final Vector2D ZERO = new Vector2D(0.0, 0.0);

    private final double x;
    private final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other) {
        return add(other.getX(), other.getY());
    }

    public Vector2D add(double x, double y) {
        return new Vector2D(getX() + x, getY() + y);
    }

    public Vector2D sub(Vector2D other) {
        return sub(other.getX(), other.getY());
    }

    public Vector2D sub(double x, double y) {
        return new Vector2D(getX() - x, getY() - y);
    }

    public Vector2D norm() {
        var length = length();
        if (length == 0.0)
            return ZERO;
        return new Vector2D(getX() / length, getY() / length);
    }

    public Vector2D scale(double scalar) {
        return new Vector2D(getX() * scalar, getY() * scalar);
    }

    public double length() {
        return Math.sqrt(x*x + y*y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("Vector2D[x=%f, y=%f]", getX(), getY());
    }
}

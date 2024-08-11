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

import org.ini4j.Ini;

import javax.annotation.Nonnull;

/**
 * The Camp class represents a camp in the game. It is associated with a
 * specific player and has a shape of a rectangle.
 *
 * @author karlz
 */
public class Camp {

    private final @Nonnull Player player;
    private final @Nonnull Properties properties;

    private @Nonnull Vector2D position = Vector2D.ZERO;

    /**
     * Constructs a new Camp object associated with the specified player and
     * positioned at the given position.
     *
     * @param player     the player that owns the camp
     * @param properties the properties of the camp
     */
    public Camp(@Nonnull Player player, @Nonnull Properties properties) {
        this.player = player;
        this.properties = properties;

        player.setCamp(this);
        player.getGame().getCamps().add(this);
    }

    public static class Properties {

        public static final String SECTION = "camp";

        public static Camp.Properties from(Ini ini) {
            return new Camp.Properties(
                    ini.fetch(SECTION, "width", double.class),
                    ini.fetch(SECTION, "height", double.class)
            );
        }

        private final double width;
        private final double height;

        public Properties(double width, double height) {
            this.width = width;
            this.height = height;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }

    /**
     * Checks if a given position is inside the camp.
     *
     * @param p the position to check
     * @return true if the position is inside the camp, false otherwise
     */
    public boolean contains(@Nonnull Vector2D p) {
        Vector2D deltaR = position.sub(p);
        return 2 * Math.abs(deltaR.getX()) < properties.width && 2 * Math.abs(deltaR.getY()) < properties.height;
    }

    /**
     * Returns the player associated with this camp.
     *
     * @return the player
     */
    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public Properties getProperties() {
        return properties;
    }

    /**
     * Returns the position of the camp.
     *
     * @return the position
     */
    @Nonnull
    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(@Nonnull Vector2D position) {
        this.position = position;
    }
}

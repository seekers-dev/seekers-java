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
 * The TorusMap class provides utility methods for handling positions and
 * distances on a torus-shaped map.
 * 
 * @author karlz
 */
public class Torus {

	private final Properties properties;

	public Torus(Properties properties) {
		this.properties = properties;
	}

	public static class Properties {

		public static final String SECTION = "map";

		public static Properties from(Ini config) {
			return new Properties(
				config.fetch(SECTION, "width", double.class),
				config.fetch(SECTION, "height", double.class)
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
	 * Adjusts the position of a Physical object to the normalized position on the
	 * torus map.
	 * 
	 * @param physical The Physical object to adjust the position for.
	 */
	public void normPosition(@Nonnull Physical<?> physical) {
		Vector2D p = physical.getPosition();

		physical.setPosition(new Vector2D(norm(p.getX(), 0, getProperties().getWidth()),
				norm(p.getY(), 0, getProperties().getHeight())));
	}

	private static double norm(double v, double min, double max) {
		if (min > v) return max + v - min;
		if (max < v) return min + v - max;
		return v;
	}

	private static double distance(double p0, double p1, double d) {
		double temp = Math.abs(p0 - p1);
		return Math.min(temp, d - temp);
	}

	/**
	 * Calculates the torus distance between two positions.
	 * 
	 * @param p0 The first position.
	 * @param p1 The second position.
	 * @return The torus distance between the two positions.
	 */
	public double getDistance(@Nonnull Vector2D p0, @Nonnull Vector2D p1) {
		return new Vector2D(distance(p0.getX(), p1.getX(), getProperties().getWidth()),
				distance(p0.getY(), p1.getY(), getProperties().getHeight())).length();
	}

	private static double difference(double p0, double p1, double d) {
		double temp = Math.abs(p0 - p1);
		return (temp < d - temp) ? p1 - p0 : p0 - p1;
	}

	/**
	 * Calculates the difference between two positions on the torus map.
	 * 
	 * @param p0 The first position.
	 * @param p1 The second position.
	 * @return The torus difference between the two positions.
	 */
	@Nonnull
	public Vector2D getDifference(@Nonnull Vector2D p0, @Nonnull Vector2D p1) {
		return new Vector2D(difference(p0.getX(), p1.getX(), getProperties().getWidth()),
				difference(p0.getY(), p1.getY(), getProperties().getHeight()));
	}

	/**
	 * Calculates the torus direction from one position to another.
	 * 
	 * @param p0 The starting position.
	 * @param p1 The target position.
	 * @return The torus direction from the starting position to the target
	 *         position.
	 */
	@Nonnull
	public Vector2D getDirection(@Nonnull Vector2D p0, @Nonnull Vector2D p1) {
		return getDifference(p0, p1).norm();
	}

	/**
	 * Generates a random position on the torus map.
	 * 
	 * @return A random position on the torus map.
	 */
	@Nonnull
	public Vector2D getRandomPosition() {
		return new Vector2D(Math.random() * getProperties().getWidth(), Math.random() * getProperties().getHeight());
	}

	/**
	 * Returns the diameter of the torus map.
	 * 
	 * @return The diameter of the torus map.
	 */
	public double getDiameter() {
		return Math.hypot(getProperties().getWidth(), getProperties().getHeight());
	}

	public Properties getProperties() {
		return properties;
	}
}

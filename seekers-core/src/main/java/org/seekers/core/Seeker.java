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

import org.apiguardian.api.API;
import org.ini4j.Ini;

import javax.annotation.Nonnull;

/**
 * The Seeker class represents a seeker in the game.
 *
 * @author Karl Zschiebsch
 */
@API(since = "0.1.0", status = API.Status.STABLE)
public class Seeker extends Physical<Seeker.Properties> {

    private final @Nonnull Player player;

    private @Nonnull Vector2D target = getPosition();

    private double magnet = 0.0;
    private int disabledCounter = 0;

    /**
     * Constructs a new instance of the Seeker class.
     *
     * @param player   The Player object associated with the Seeker.
     */
    public Seeker(@Nonnull Player player, Properties properties) {
        super(player.getGame(), properties);
        this.player = player;
        player.getSeekers().put(toString(), this);
        getGame().getSeekers().add(this);
    }

    public static class Properties extends Physical.Properties {

        public static final String SECTION = "seeker";

        public static Seeker.Properties from(Ini ini) {
            return new Seeker.Properties(
                ini.fetch(SECTION, "mass", double.class),
                ini.fetch(SECTION, "radius", double.class),
                ini.fetch(SECTION, "thrust", double.class),
                ini.fetch(SECTION, "friction", double.class),
                ini.fetch(SECTION, "magnet-slowdown", double.class),
                ini.fetch(SECTION, "disabled-time", int.class)
            );
        }

        private final double magnetSlowdown;
        private final int disabledTime;

        public Properties(double mass, double radius, double thrust, double friction, double magnetSlowdown, int disabledTime) {
            super(mass, radius, thrust, friction);
            this.magnetSlowdown = magnetSlowdown;
            this.disabledTime = disabledTime;
        }

        public double getMagnetSlowdown() {
            return magnetSlowdown;
        }

        public int getDisabledTime() {
            return disabledTime;
        }
    }

    @Override
    public void update() {
        super.update();
        if (isSeekerDisabled()) {
            disabledCounter = Math.max(disabledCounter - 1, 0);
        }
    }

    @Override
    public void accelerate() {
        if (!isSeekerDisabled()) {
            setAcceleration(getGame().getTorus().getDirection(getPosition(), getTarget()));
        } else {
            setAcceleration(Vector2D.ZERO);
        }
    }

    @Override
    public void collision(@Nonnull Physical<?> another, double minDistance) {
        if (another instanceof Seeker) {
            Seeker collision = (Seeker) another;
            if (collision.isSeekerDisabled()) {
                disable();
            } else if (magnet != 0) {
                disable();
                if (collision.magnet != 0)
                    collision.disable();
            } else if (collision.magnet != 0) {
                collision.disable();
            } else {
                disable();
                collision.disable();
            }
        }

        super.collision(another, minDistance);
    }

    /**
     * Calculates the magnetic force between the Seeker and a given position.
     *
     * @param p The position to calculate the magnetic force with.
     * @return The magnetic force vector.
     */
    @Nonnull
    public Vector2D getMagneticForce(@Nonnull Vector2D p) {
        double r = getGame().getTorus().getDistance(getPosition(), p) / getGame().getTorus().getDiameter() * 10;
        Vector2D d = getGame().getTorus().getDirection(getPosition(), p);
        double s = (r < 1) ? Math.exp(1 / (Math.pow(r, 2) - 1)) : 0;
        return (isSeekerDisabled()) ? Vector2D.ZERO : d.scale(-getMagnet() * s);
    }

    /**
     * Returns the thrust of the Seeker, taking into account the magnet's effect.
     *
     * @return The thrust of the Seeker.
     */
    @Override
    public double getThrust() {
        return getProperties().getThrust() * (magnet != 0 ? getProperties().getMagnetSlowdown() : 1);
    }

    /**
     * Returns the Player object associated with the Seeker.
     *
     * @return The Player object associated with the Seeker.
     */
    @Nonnull
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the magnet value of the Seeker.
     *
     * @return The magnet value of the Seeker.
     */
    public double getMagnet() {
        return magnet;
    }

    /**
     * Sets the magnet value of the Seeker.
     *
     * @param magnet The magnet value to set.
     */
    public void setMagnet(double magnet) {
        if (!isSeekerDisabled()) {
            this.magnet = Math.max(Math.min(magnet, 1), -8);
        }
    }

    public void changeMagnet(double magnet) {
        setMagnet(magnet);
    }

    /**
     * Disables the Seeker.
     */
    public void disable() {
        if (!isSeekerDisabled()) {
            disabledCounter = getProperties().getDisabledTime();
            setMagnet(0.0);
        }
    }

    /**
     * Checks if the Seeker is disabled.
     *
     * @return True if the Seeker is disabled, false otherwise.
     */
    public boolean isSeekerDisabled() {
        return disabledCounter > 0;
    }

    /**
     * Returns the target position of the Seeker.
     *
     * @return The target position of the Seeker.
     */
    @Nonnull
    public Vector2D getTarget() {
        return target;
    }

    /**
     * Sets the target position of the Seeker.
     *
     * @param target The target position to set.
     */
    public void setTarget(@Nonnull Vector2D target) {
        this.target = target;
    }

    public void changeTarget(@Nonnull Vector2D target) {
        setTarget(target);
    }
}

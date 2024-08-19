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

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;

/**
 *
 * @param <P> type of the properties
 * @author Karl Zschiebsch
 */
@API(since = "0.1.0", status = API.Status.STABLE)
public abstract class Physical<P extends Physical.Properties> implements Entity {

    private final @Nonnull Game game;
    private @Nonnull Vector2D acceleration = Vector2D.ZERO;
    private @Nonnull Vector2D velocity = Vector2D.ZERO;
    private @Nonnull Vector2D position = Vector2D.ZERO;

    private final @Nonnull P properties;

    /**
     * Constructs a new instance of the Physical class.
     *
     * @param game The Game object associated with the Physical object.
     */
    protected Physical(@Nonnull Game game, @Nonnull P properties) {
        this.game = game;
        this.properties = properties;

        setPosition(game.getTorus().getRandomPosition());
        getGame().getEntities().add(this);
    }

    public static class Properties {
        private final double mass;
        private final double radius;
        private final double thrust;
        private final double friction;

        public Properties(double mass, double radius, double thrust, double friction) {
            this.mass = mass;
            this.radius = radius;
            this.thrust = thrust;
            this.friction = friction;
        }

        public double getMass() {
            return mass;
        }

        public double getRadius() {
            return radius;
        }

        public double getThrust() {
            return thrust;
        }

        public double getFriction() {
            return friction;
        }
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void update() {
        accelerate();
        velocity();
        displacement();
        checks();
    }

    public abstract void accelerate();

    public void velocity() {
        setVelocity(getVelocity().scale(1 - properties.getFriction()));
        setVelocity(getVelocity().add(getAcceleration().scale(getThrust())));
    }

    public void displacement() {
        setPosition(getPosition().add(getVelocity()));
        getGame().getTorus().normPosition(this);
    }

    /**
     * Performs collision checks with other Physical objects.
     */
    private void checks() {
        final List<Entity> entities = getGame().getEntities();
        for (Entity entity : entities) {
            if (entity == this || !(entity instanceof Physical))
                continue;
            Physical<?> physical = (Physical<?>) entity;
            double min = properties.getRadius() + physical.properties.getRadius();
            double dist = getGame().getTorus().getDistance(position, physical.position);
            if (min > dist) {
                collision(physical, min);
            }
        }
    }

    /**
     * Handles a collision with another Physical object.
     *
     * @param another     The Physical object with which a collision occurred.
     * @param minDistance The minimum distance required for a collision to occur.
     */
    @OverridingMethodsMustInvokeSuper
    public void collision(@Nonnull Physical<?> another, double minDistance) {
        Vector2D distance = getGame().getTorus().getDifference(getPosition(), another.getPosition());

        Vector2D deltaR = distance.norm();
        Vector2D deltaV = another.getVelocity().sub(getVelocity());

        double dualV = deltaV.getX() * deltaR.getX() + deltaV.getY() * deltaR.getY();
        double dualM = 2 / (properties.getMass() + another.properties.getMass());

        if (dualV < 0) {
            setVelocity(getVelocity().add(deltaR.scale(another.properties.getMass() * dualM * dualV)));
            another.setVelocity(another.getVelocity().sub(deltaR.scale(properties.getMass() * dualM * dualV)));
        }
        double ddn = distance.getX() * deltaR.getX() + distance.getY() * deltaR.getY();
        if (ddn < minDistance) {
            setPosition(getPosition().add(deltaR.scale(ddn - minDistance)));
            another.setPosition(another.getPosition().sub(deltaR.scale(ddn - minDistance)));
        }
    }

    /**
     * Retrieves the Game object associated with the Physical object.
     *
     * @return The Game object.
     */
    @Nonnull
    public Game getGame() {
        return game;
    }

    /**
     * Retrieves the position of the Physical object.
     *
     * @return The position vector.
     */
    @Nonnull
    public Vector2D getPosition() {
        return position;
    }

    /**
     * Sets the position of the Physical object.
     *
     * @param position The new position vector.
     */
    public void setPosition(@Nonnull Vector2D position) {
        this.position = position;
    }

    /**
     * Retrieves the velocity of the Physical object.
     *
     * @return The velocity vector.
     */
    @Nonnull
    public Vector2D getVelocity() {
        return velocity;
    }

    /**
     * Sets the velocity of the Physical object.
     *
     * @param velocity The new velocity vector.
     */
    public void setVelocity(@Nonnull Vector2D velocity) {
        this.velocity = velocity;
    }

    /**
     * Retrieves the acceleration of the Physical object.
     *
     * @return The acceleration vector.
     */
    @Nonnull
    public Vector2D getAcceleration() {
        return acceleration;
    }

    /**
     * Sets the acceleration of the Physical object.
     *
     * @param acceleration The new acceleration vector.
     */
    public void setAcceleration(@Nonnull Vector2D acceleration) {
        this.acceleration = acceleration;
    }

    public double getThrust() {
        return properties.getThrust();
    }

    @Nonnull
    public P getProperties() {
        return properties;
    }
}

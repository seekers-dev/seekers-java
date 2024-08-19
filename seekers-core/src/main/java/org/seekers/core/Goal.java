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
import javax.annotation.Nullable;
import java.util.List;

/**
 * A goal is a physical object that can be adopted by a camp and used for
 * scoring. It keeps track of the time owned by a camp and triggers scoring when
 * the required time is reached.
 *
 * @author karlz
 */
@API(status = API.Status.STABLE)
public class Goal extends Physical<Goal.Properties> {

    private @Nullable Camp capture;
    private int timeOwned = 0;

    /**
     * Constructs a new instance of the Goal class.
     *
     * @param game     The Game object associated with the Goal object.
     */
    public Goal(@Nonnull Game game, @Nonnull Properties properties) {
        super(game, properties);
        getGame().getGoals().add(this);
    }

    public static class Properties extends Physical.Properties {

        public static final String SECTION = "goal";

        public static Properties from(Ini ini) {
            return new Properties(
                    ini.fetch(SECTION, "mass", double.class),
                    ini.fetch(SECTION, "radius", double.class),
                    ini.fetch(SECTION, "thrust", double.class),
                    ini.fetch(SECTION, "friction", double.class),
                    ini.fetch(SECTION, "scoring-time", double.class)
            );
        }

        private final double scoringTime;

        public Properties(double mass, double radius, double thrust, double friction, double scoringTime) {
            super(mass, radius, thrust, friction);
            this.scoringTime = scoringTime;
        }

        public double getScoringTime() {
            return scoringTime;
        }
    }

    @Override
    public void update() {
        super.update();
        adopt();
    }

    @Override
    public void accelerate() {
        Vector2D force = Vector2D.ZERO;
        for (Seeker seeker : List.copyOf(getGame().getSeekers())) {
            force = force.add(seeker.getMagneticForce(getPosition()));
        }
        setAcceleration(force);
    }

    /**
     * Adopts the Goal object to a camp and checks for scoring.
     */
    private void adopt() {
        for (Camp camp : getGame().getCamps()) {
            if (camp.contains(getPosition())) {
                if (this.capture == camp) {
                    setTimeOwned(getTimeOwned() + 1);
                    if (timeOwned >= getProperties().scoringTime) {
                        score(camp.getPlayer());
                        return;
                    }
                } else {
                    this.capture = camp;
                    setTimeOwned(0);
                }
            }
        }
    }

    /**
     * Scores a goal for the given player and resets the Goal object.
     *
     * @param player The player who scored the goal.
     */
    private void score(Player player) {
        player.score();
        reset();
    }

    /**
     * Resets the state of the Goal object.
     */
    protected void reset() {
        setPosition(getGame().getTorus().getRandomPosition());
        capture = null;
        setTimeOwned(0);
    }

    /**
     * @return the time this goal was inside the current camp, or 0 if it was not inside a camp at all.
     */
    public int getTimeOwned() {
        return timeOwned;
    }

    /**
     * Sets the time owned by a camp.
     *
     * @param timeOwned the time owned by a camp
     */
    public void setTimeOwned(int timeOwned) {
        this.timeOwned = timeOwned;
    }

    @Nullable
    public Camp getCapture() {
        return capture;
    }
}

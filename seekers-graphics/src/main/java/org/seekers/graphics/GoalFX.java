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

package org.seekers.graphics;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.apiguardian.api.API;
import org.seekers.core.Camp;
import org.seekers.core.Goal;
import org.seekers.core.Vector2D;

import javax.annotation.Nonnull;

/**
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public class GoalFX extends Goal {

    private final @Nonnull Circle graphic = new Circle(getProperties().getRadius(), Color.WHITE);

    public GoalFX(@Nonnull GameFX game, @Nonnull Properties properties) {
        super(game, properties);

        game.getBack().getChildren().add(getGraphic());
    }

    @Override
    protected void reset() {
        new GoalAnimation((GameFX) getGame());
        super.reset();
    }

    @Nonnull
    public Circle getGraphic() {
        return graphic;
    }

    @Override
    public void setPosition(@Nonnull Vector2D position) {
        super.setPosition(position);
        Platform.runLater(() -> {
            getGraphic().setCenterX(position.getX());
            getGraphic().setCenterY(position.getY());
        });
    }

    @Override
    public void setTimeOwned(int timeOwned) {
        super.setTimeOwned(timeOwned);
        Platform.runLater(() -> {
            if (timeOwned == 0) {
                getGraphic().setFill(Color.WHITE);
            } else {
                final Camp checked = getCapture();
                if (checked != null) {
                    Color color = Color.web(checked.getPlayer().getColor());
                    getGraphic().setFill(Color.color(
                            1 + (color.getRed() - 1) * timeOwned / getProperties().getScoringTime(),
                            1 + (color.getGreen() - 1) * timeOwned / getProperties().getScoringTime(),
                            1 + (color.getBlue() - 1) * timeOwned / getProperties().getScoringTime()));
                }
            }
        });
    }

    /**
     * The scoring animation for goals.
     *
     * @author Karl Zschiebsch
     * @since 0.1.0
     */
    @API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
    public class GoalAnimation extends Animation {

        private static final double ANIMATION_RANGE = 50.0;
        private final Circle wave = new Circle(0);

        /**
         * Creates a new scoring animation.
         *
         * @param game the game
         */
        public GoalAnimation(GameFX game) {
            super(game);
            if (Platform.isFxApplicationThread()) place();
            else Platform.runLater(this::place);
        }

        @Override
        public void update() {
            if (Platform.isFxApplicationThread()) wave();
            else Platform.runLater(this::wave);
        }

        private void place() {
            getChildren().add(wave);
            setLayoutX(getPosition().getX());
            setLayoutY(getPosition().getY());
            Color color = getCapture() != null ? Color.web(getCapture().getPlayer().getColor()) : Color.WHITE;
            wave.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.25));
            wave.setStroke(color);
            wave.setStrokeWidth(2);
        }

        private void wave() {
            var next = wave.getRadius() + 0.75;
            if (next < ANIMATION_RANGE) {
                wave.setRadius(next);
                wave.setStrokeWidth(1 + next / ANIMATION_RANGE);
            } else {
                destroy();
            }
        }
    }
}

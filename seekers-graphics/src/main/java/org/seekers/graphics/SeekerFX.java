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
import org.seekers.core.Seeker;
import org.seekers.core.Vector2D;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public class SeekerFX extends Seeker {

    private final Circle graphic = new Circle(getProperties().getRadius());
    private final SeekerAnimation animation;

    /**
     * Constructs a new instance of the Seeker class.
     *
     * @param player     The Player object associated with the Seeker.
     * @param properties The properties
     */
    public SeekerFX(@Nonnull PlayerFX player, Properties properties) {
        super(player, properties);
        this.animation = new SeekerAnimation(player.getGame());

        getGraphic().setFill(Color.web(player.getColor()));
        player.getGame().getBack().getChildren().addAll(getGraphic(), animation);
    }

    @Override
    public void setPosition(@Nonnull Vector2D position) {
        super.setPosition(position);
        if (getGraphic() != null && getAnimation() != null) {
            if (Platform.isFxApplicationThread()) reposition();
            else Platform.runLater(this::reposition);
        }
    }

    private void reposition() {
        final var position = getPosition();
        getGraphic().setCenterX(position.getX());
        getGraphic().setCenterY(position.getY());

        getAnimation().setLayoutX(position.getX());
        getAnimation().setLayoutY(position.getY());
    }

    @Override
    public void setMagnet(double magnet) {
        super.setMagnet(magnet);
        Platform.runLater(() -> getAnimation().setVisible(magnet != 0));
    }

    @Override
    public void disable() {
        super.disable();
        if (Platform.isFxApplicationThread()) fxDisable();
        else Platform.runLater(this::fxDisable);
    }

    private void fxDisable() {
        getGraphic().setFill(disabled);
        getAnimation().setVisible(false);
    }

    private void fxEnable() {
        getGraphic().setFill(activated);
        getAnimation().setVisible(true);
    }

    @Override
    public boolean isSeekerDisabled() {
        final boolean state = super.isSeekerDisabled();
        if (Platform.isFxApplicationThread()) {
            if (state) {
                fxEnable();
            } else {
                fxDisable();
            }
        } else {
            Platform.runLater(() -> {
                if (state) {
                    fxEnable();
                } else {
                    fxDisable();
                }
            });
        }
        return state;
    }

    private @Nonnull Color activated = Color.WHITE;
    private @Nonnull Color disabled = Color.GRAY;

    public void setColor(Color color) {
        this.activated = color;
        this.disabled = color.darker().darker().darker();

        getGraphic().setFill(color);
        getAnimation().setColor(color);
    }

    public Circle getGraphic() {
        return graphic;
    }

    public SeekerAnimation getAnimation() {
        return animation;
    }

    /**
     * @author Karl Zschiebsch
     * @since 0.1.0
     */
    @API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
    public class SeekerAnimation extends Animation {

        private final @Nonnull List<Circle> indicators = List.of(
                new Circle(SeekerFX.this.getProperties().getRadius() + getAnimationRange() / 3),
                new Circle(SeekerFX.this.getProperties().getRadius() + getAnimationRange() * 2 / 3) ,
                new Circle(SeekerFX.this.getProperties().getRadius() + getAnimationRange())
        );
        private final @Nonnull List<Double> expansions = new ArrayList<>(List.of(
                getAnimationRange() / 3, getAnimationRange() * 2 / 3, getAnimationRange()
        ));

        protected SeekerAnimation(@Nonnull GameFX game) {
            super(game);
            for (var indicator : indicators) {
                indicator.setFill(Color.TRANSPARENT);
                indicator.setStrokeWidth(2);
                indicator.setStroke(Color.web(getPlayer().getColor()));
                getChildren().add(indicator);
            }
            setVisible(false);
        }

        private int frameTime = 8;

        @Override
        public void update() {
            if (Platform.isFxApplicationThread()) expand();
            else Platform.runLater(this::expand);
        }

        public void expand() {
            frameTime--;
            if (frameTime < 0) {
                for (int i = 0; i < expansions.size(); i++) {
                    var range = expansions.get(i) - Math.signum(getMagnet());
                    if (range > getAnimationRange()) range -= getAnimationRange();
                    if (range < 0) range += getAnimationRange();
                    expansions.set(i, range);
                    indicators.get(i).setRadius(range + SeekerFX.this.getProperties().getRadius());
                    frameTime = 8;
                }
            }
        }

        @Override
        public void destroy() {
            throw new UnsupportedOperationException();
        }

        public void setColor(Color color) {
            for (var indicator : indicators) {
                indicator.setStroke(color);
            }
        }

        /**
         * Returns the animation range of the Seeker.
         *
         * @return The animation range of the Seeker.
         */
        public double getAnimationRange() {
            return 26;
        }
    }
}

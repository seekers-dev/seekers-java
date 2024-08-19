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
import javafx.scene.shape.Rectangle;
import org.apiguardian.api.API;
import org.seekers.core.Camp;
import org.seekers.core.Vector2D;

import javax.annotation.Nonnull;

/**
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public class CampFX extends Camp {

    private final Rectangle graphic = new Rectangle();

    /**
     * Constructs a new Camp object associated with the specified player and positioned at the given position.
     *
     * @param player     the player that owns the camp
     * @param properties the properties of the camp
     */
    public CampFX(@Nonnull PlayerFX player,
                  @Nonnull Properties properties) {
        super(player, properties);

        getGraphic().setWidth(properties.getWidth());
        getGraphic().setHeight(properties.getHeight());
        getGraphic().setFill(Color.TRANSPARENT);
        getGraphic().setStroke(Color.web(player.getColor()));
        getGraphic().setStrokeWidth(6);

        player.getGame().getBack().getChildren().add(getGraphic());
    }

    public Rectangle getGraphic() {
        return graphic;
    }

    @Override
    public void setPosition(@Nonnull Vector2D position) {
        super.setPosition(position);
        Platform.runLater(() -> {
            getGraphic().setLayoutX(position.getX() - getProperties().getWidth() * 0.5);
            getGraphic().setLayoutY(position.getY() - getProperties().getHeight() * 0.5);
        });
    }
}

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
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apiguardian.api.API;
import org.seekers.core.Camp;
import org.seekers.core.Player;
import org.seekers.core.Seeker;

import javax.annotation.Nonnull;

/**
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public class PlayerFX extends Player {
    private final Label graphics = new Label();

    /**
     * Constructs a new instance of the Player class.
     *
     * @param game The Game object associated with the Player object.
     */
    public PlayerFX(@Nonnull GameFX game) {
        super(game);

        getGraphics().setFont(Font.loadFont(getClass().getResourceAsStream("PixelFont.otf"), 24));
        game.getInfo().getChildren().add(getGraphics());
    }

    @Override
    public void score() {
        super.score();
        renderLabel();
    }

    private void renderLabel() {
        Platform.runLater(() -> getGraphics().setText(String.format("%4d %s", getScore(), getName())));
    }

    @Override
    public void setName(@Nonnull String name) {
        super.setName(name);
        renderLabel();
    }

    @Override
    public void setColor(@Nonnull String color) {
        super.setColor(color);
        Platform.runLater(() -> {
            Color web = Color.web(color);
            getGraphics().setTextFill(web);
            for (Seeker seeker : getSeekers().values()) {
                if (seeker instanceof SeekerFX) {
                    ((SeekerFX) seeker).setColor(web);
                }
            }
            Camp camp = getCamp();
            if (camp instanceof CampFX) {
                ((CampFX) camp).getGraphic().setStroke(web);
            }
        });
    }

    @Nonnull
    @Override
    public GameFX getGame() {
        return (GameFX) super.getGame();
    }

    public Label getGraphics() {
        return graphics;
    }
}

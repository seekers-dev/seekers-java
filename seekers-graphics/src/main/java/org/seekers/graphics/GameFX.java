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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apiguardian.api.API;
import org.ini4j.Ini;
import org.seekers.core.*;

import javax.annotation.Nonnull;

/**
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public class GameFX extends Game {

    public static synchronized GameFX create(Ini config) {
        GameFX game = new GameFX(Game.Properties.from(config), new Torus(Torus.Properties.from(config)));
        Goal.Properties goals = Goal.Properties.from(config);
        for (int i = 0; i < game.getProperties().getGoals(); i++) {
            new GoalFX(game, goals);
        }
        Seeker.Properties seekers = Seeker.Properties.from(config);
        Camp.Properties camps = Camp.Properties.from(config);
        for (int i = 0; i < game.getProperties().getPlayers(); i++) {
            PlayerFX player = new PlayerFX(game);
            CampFX camp = new CampFX(player, camps);
            camp.setPosition(new Vector2D(game.getTorus().getProperties().getWidth() * 0.5,
                    game.getTorus().getProperties().getHeight() * (game.getPlayers().size() - 0.5) / game.getProperties().getPlayers()));
            for (int j = 0; j < game.getProperties().getSeekers(); j++) {
                new SeekerFX(player, seekers);
            }
        }
        return game;
    }

    // Graphics
    private final @Nonnull Label time = new Label();
    private final @Nonnull VBox info = new VBox(5);
    private final @Nonnull Group front = new Group();
    private final @Nonnull Group back = new Group();
    private final @Nonnull Stage stage = new Stage();
    private final @Nonnull Scene scene;

    public GameFX(Properties properties, Torus torus) {
        super(properties, torus);

        BorderPane parent = new BorderPane();
        parent.setTop(getInfo());
        parent.getChildren().addAll(getBack(), getFront());
        parent.setBottom(getTime());
        parent.setBackground(new Background(new BackgroundFill(Color.gray(.1), null, null)));

        this.scene = new Scene(parent, 768, 768, true, SceneAntialiasing.BALANCED);
        this.stage.setScene(getScene());
        this.stage.setOnCloseRequest(e -> setGameState(State.FINISHED));
        this.stage.show();

        getTime().setFont(Font.loadFont(getClass().getResourceAsStream("PixelFont.otf"), 16));
        getTime().setTextFill(Color.WHITESMOKE);
        getInfo().setPadding(new Insets(10));
        setOnGameFinished(e -> Platform.runLater(stage::hide));
    }

    @Override
    public void updateAll() {
        super.updateAll();
        getTime().setText(String.format("[%5d]", getPassedPlaytime()));
    }

    @Override
    public void play() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(getProperties().getTickDuration()), e -> updateAll()));
        timeline.setCycleCount(getProperties().getPlaytime());
        timeline.play();
        setOnGameFinished(g -> timeline.stop());
    }

    @Nonnull
    public Scene getScene() {
        return scene;
    }

    @Nonnull
    public VBox getInfo() {
        return info;
    }

    @Nonnull
    public Group getBack() {
        return back;
    }

    @Nonnull
    public Group getFront() {
        return front;
    }

    @Nonnull
    public Label getTime() {
        return time;
    }
}

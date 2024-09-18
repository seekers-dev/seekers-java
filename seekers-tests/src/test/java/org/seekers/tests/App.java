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

package org.seekers.tests;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ini4j.Ini;
import org.seekers.graphics.GameFX;
import org.seekers.server.SeekersServer;
import org.seekers.server.Tournament;

import java.nio.file.Path;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Ini config = GameFX.config();
        SeekersServer server = new SeekersServer(config, GameFX::create);

        server.start();

        Tournament tournament = new Tournament();
        tournament.matchAll(Path.of("external", "players").toFile());
        server.playMatch(tournament.getMatches().get(0));

        // server.playMatch(List.of("players/ai-undefined.py", "players/ai-tutorial.py"));
    }
}

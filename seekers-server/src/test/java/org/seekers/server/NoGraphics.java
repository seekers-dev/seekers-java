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

package org.seekers.server;

import org.ini4j.Ini;
import org.seekers.core.Game;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NoGraphics {

    public static void main(String[] args) throws IOException {
        Ini config = new Ini(new File("config.ini"));
        SeekersServer server = new SeekersServer(config, Game::create);

        server.start();
        server.playMatch(List.of("players/ai-undefined.py", "players/ai-tutorial.py"));

    }
}
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seekers.server.Tournament;

class TestTournament {

    @Test
    void tournament() {
        Assertions.assertDoesNotThrow(() -> {
            Tournament tournament = new Tournament();
            tournament.matchAll("A", "B", "C");
            tournament.save();
        });
    }

    @Test
    void name() {
        Tournament tournament = new Tournament();
        System.err.println(tournament.getMeta());
    }
}

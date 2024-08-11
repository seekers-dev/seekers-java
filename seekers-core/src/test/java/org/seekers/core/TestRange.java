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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestRange {

    @Test
    void from0To4() {
        var array = new int[5];
        var pos = 0;
        for (int i = 0; i < 5; ) {
            array[pos++] = i++;
        }

        Assertions.assertArrayEquals(new int[] {0, 1, 2, 3, 4}, array);
    }

    @Test
    void from1To5() {
        var array = new int[5];
        var pos = 0;
        for (int i = 0; i < 5; ) {
            array[pos++] = ++i;
        }

        Assertions.assertArrayEquals(new int[] {1, 2, 3, 4, 5}, array);
    }
}

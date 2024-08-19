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

import javafx.scene.layout.Pane;
import org.apiguardian.api.API;
import org.seekers.core.Entity;

import javax.annotation.Nonnull;

/**
 * Base class for all animations.
 *
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.STABLE)
public abstract class Animation extends Pane implements Entity {

	private final @Nonnull GameFX game;

	protected Animation(@Nonnull GameFX game) {
		this.game = game;
		game.getEntities().add(this);
		game.getFront().getChildren().add(this);
	}

	public void destroy() {
		game.getFront().getChildren().remove(this);
		game.getEntities().remove(this);
	}

	@Nonnull
	public GameFX getGame() {
		return game;
	}
}

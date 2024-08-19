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

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Player class represents a player in the game.
 * 
 * @author karlz
game.getFront().getChildren().add(this);
 */
@API(status = API.Status.STABLE)
public class Player {

	private final @Nonnull Game game;
	private final @Nonnull Map<String, Seeker> seekers = new LinkedHashMap<>();

	private @Nullable Camp camp;
	private @Nonnull String name;
	private @Nonnull String color;

	private int score;

	/**
	 * Constructs a new instance of the Player class.
	 *
	 * @param game The Game object associated with the Player object.
	 */
	public Player(@Nonnull Game game) {
		this.game = game;
		this.name = "Player " + hashCode();
		this.color = "0xffffff";
		getGame().getPlayers().add(this);
	}

	/**
	 * Increases the score of the Player by 1.
	 */
	public void score() {
		score++;
	}

	/**
	 * Gets the map of Seekers associated with the Player.
	 *
	 * @return The map of Seekers associated with the Player.
	 */
	@Nonnull
	public Map<String, Seeker> getSeekers() {
		return seekers;
	}

	/**
	 * Gets the Game object associated with the Player.
	 *
	 * @return The Game object associated with the Player.
	 */
	@Nonnull
	public Game getGame() {
		return game;
	}

	/**
	 * Gets the Camp associated with the Player.
	 *
	 * @return The Camp associated with the Player.
	 */
	@Nullable
	public Camp getCamp() {
		return camp;
	}

	/**
	 * Sets the Camp associated with the Player.
	 *
	 * @param camp The Camp to set.
	 */
	public void setCamp(@Nonnull Camp camp) {
		this.camp = camp;
	}

	/**
	 * Gets the name of the Player.
	 *
	 * @return The name of the Player.
	 */
	@Nonnull
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Player.
	 *
	 * @param name The name to set.
	 */
	public void setName(@Nonnull String name) {
		this.name = name;
	}

	/**
	 * Gets the color of the Player.
	 *
	 * @return The color of the Player.
	 */
	@Nonnull
	public String getColor() {
		return color;
	}

	public void setColor(@Nonnull String color) {
		this.color = color;
	}

	/**
	 * Gets the score of the Player.
	 *
	 * @return The score of the Player.
	 */
	public int getScore() {
		return score;
	}
}

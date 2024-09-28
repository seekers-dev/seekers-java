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

import com.google.gson.Gson;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a Seekers Tournament.
 *
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public class Tournament implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(Tournament.class);
	private static final Gson gson = new Gson();

	private final @Nonnull List<List<String>> matches = new LinkedList<>();
	private final @Nonnull Map<String, List<Integer>> results = new HashMap<>();

	private final Metadata meta = new Metadata();

	public void matchAll(File folder) {
		String[] files = folder.list((File dir, String name) -> name.startsWith("ai") && !name.endsWith(".log"));
		if (files != null) {
			for (int p = 0, size = files.length; p < size; p++) {
				for (int m = p + 1; m < size; m++) {
					matches.add(List.of(folder + "/" + files[p], folder + "/" + files[m]));
				}
			}
		} else {
			logger.error("No AIs found in folder, maybe folder or files are missing?");
		}
	}

	public void matchAll(String... players) {
		for (int p = 0, size = players.length; p < size; p++) {
			for (int m = p + 1; m < size; m++) {
				matches.add(List.of(players[p], players[m]));
			}
		}
	}

	public void match(String player0, String player1) {
		matches.add(List.of(player0, player1));
	}

	public void match(String... players) {
		matches.add(List.of(players));
	}

	public void save() throws IOException {
		File folder = new File("results");
		if (!folder.exists() && !folder.mkdir()) {
			logger.error("Failed to create results folder");
		}
		File file = new File("results/" +  meta + ".json");
		System.err.println("results/" +  meta + ".json");
		if (!file.exists() && !file.createNewFile()) {
			logger.error("Failed to create log file");
		}
		try (FileOutputStream stream = new FileOutputStream(file)) {
			stream.write(gson.toJson(this).getBytes());
		}
    }

	public Metadata getMeta() {
		return meta;
	}

	@Nonnull
	public List<List<String>> getMatches() {
		return matches;
	}

	@Nonnull
	public Map<String, List<Integer>> getResults() {
		return results;
	}

	public static class Metadata {

		private final @Nonnull Date date = new Date();
		private String name = "";
		private String description = "";
		private String tournament = "";

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public void setTournament(String tournament) {
			this.tournament = tournament;
		}

		public String getTournament() {
			return tournament;
		}

		@Nonnull
		public Date getDate() {
			return date;
		}

		@Override
		public String toString() {
			return (getName().isBlank() ? "" : getName() + "-")
					+ (getTournament().isBlank() ? "" : getTournament() + "-")
					+ String.format("%1$tY-%1$tm-%1td %1$tT", getDate());
		}
	}
}

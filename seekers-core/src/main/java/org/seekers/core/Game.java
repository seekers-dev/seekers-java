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

import org.ini4j.Ini;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Game class represents a game environment where players, seekers, goals,
 * and camps interact. It manages the game state, updates the positions of game
 * entities, and provides visual rendering.
 *
 * @author Karl Zschiebsch
 * @author Jonas Endter
 */
public class Game {

    public static Game create(Ini config) {
        Game game = new Game(Game.Properties.from(config), new Torus(Torus.Properties.from(config)));
        Goal.Properties goals = Goal.Properties.from(config);
        for (int i = 0; i < game.getProperties().getGoals(); i++) {
            new Goal(game, goals);
        }
        Camp.Properties camps = Camp.Properties.from(config);
        Seeker.Properties seekers = Seeker.Properties.from(config);
        for (int i = 0; i < game.getProperties().getPlayers(); i++) {
            Player player = new Player(game);
            Camp camp = new Camp(player, camps);
            camp.setPosition(new Vector2D(game.getTorus().getProperties().getWidth() * 0.5,
                    game.getTorus().getProperties().getHeight() * (game.getPlayers().size() - 0.5) / game.getProperties().getPlayers()));
            for (int j = 0; j < game.getProperties().getSeekers(); j++) {
                new Seeker(player, seekers);
            }
        }
        return game;
    }

    // Entities and properties
    private final @Nonnull List<Entity> entities = new ArrayList<>();
    private final @Nonnull Properties properties;
    private final @Nonnull Torus torus;

    // Game state
    private @Nonnull State state = State.PREPARING;
    private long tick = 0;

    // Cached types for fast access
    private final @Nonnull List<Player> players = new ArrayList<>();
    private final @Nonnull List<Seeker> seekers = new ArrayList<>();
    private final @Nonnull List<Goal> goals = new ArrayList<>();
    private final @Nonnull List<Camp> camps = new ArrayList<>();

    // Events
    private @Nullable Consumer<Game> onGameStarted;
    private @Nullable Consumer<Game> onGameFinished;

    /**
     * Constructs a new Game object. Initializes the game environment, creates the
     * game rendering components, and starts the game timeline.
     */
    public Game(@Nonnull Properties properties, @Nonnull Torus torus) {
        this.properties = properties;
        this.torus = torus;
    }

    /**
     * Properties for all global config attributes.
     */
    public static class Properties {

        public static final String SECTION = "global";

        public static Properties from(Ini ini) {
            return new Properties(
                    ini.fetch(SECTION, "playtime", int.class),
                    ini.fetch(SECTION, "players", int.class),
                    ini.fetch(SECTION, "seekers", int.class),
                    ini.fetch(SECTION, "goals", int.class),
                    ini.fetch(SECTION, "tick-duration", double.class)
            );
        }

        private final int playtime;
        private final int players;
        private final int seekers;
        private final int goals;
        private final double tickDuration;

        public Properties(int playtime, int players, int seekers, int goals, double tickDuration) {
            this.playtime = Util.checkNotNegative(playtime);
            this.players = Util.checkPositive(players);
            this.seekers = Util.checkPositive(seekers);
            this.goals = Util.checkPositive(goals);
            this.tickDuration = Util.checkPositive(tickDuration);
        }

        public int getPlaytime() {
            return playtime;
        }

        public int getPlayers() {
            return players;
        }

        public int getSeekers() {
            return seekers;
        }

        public int getGoals() {
            return goals;
        }

        public double getTickDuration() {
            return tickDuration;
        }
    }

    public void updateAll() {
        for (Entity entity : List.copyOf(getEntities())) {
            entity.update();
        }
        ++tick;
        if (tick >= getProperties().getPlaytime()) {
            setGameState(State.FINISHED);
        }
    }

    public void play() {
        Thread clock = new Thread() {
            @Override
            public void run() {
                while (getPassedPlaytime() < getProperties().getPlaytime()) {
                    synchronized (this) {
                        updateAll();
                        try {
                            wait((long) getProperties().getTickDuration(),
                                    (int) (getProperties().getTickDuration() - Math.round(getProperties().getTickDuration())));
                        } catch (InterruptedException ex) {
                            interrupt();
                        }
                    }
                }
            }
        };
        clock.setName("Seekers-Game (core impl)");
        clock.start();
        setOnGameFinished(e -> clock.interrupt());
    }

    /**
     * @return the list of entities
     */
    @Nonnull
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * @return the list of seekers
     */
    @Nonnull
	public List<Seeker> getSeekers() {
        return seekers;
    }

    /**
     * @return the list of players
     */
    @Nonnull
	public List<Player> getPlayers() {
        return players;
    }

    /**
     * @return the list of goals
     */
    @Nonnull
	public List<Goal> getGoals() {
        return goals;
    }

    /**
     * @return the list of camps
     */
    @Nonnull
	public List<Camp> getCamps() {
        return camps;
    }

    /**
     * @return the passed playtime
     */
    public long getPassedPlaytime() {
        return tick;
    }

    @Nonnull
    public Properties getProperties() {
        return properties;
    }

    @CheckReturnValue
    @Nonnull
    public Torus getTorus() {
        return torus;
    }

    @CheckReturnValue
    @Nonnull
    public State getGameState() {
        return state;
    }

    public void setGameState(@Nonnull State state) {
        if (getGameState() == State.PREPARING && state == State.RUNNING && getOnGameStarted() != null)
            getOnGameStarted().accept(this);
        if (getGameState() == State.RUNNING && state == State.FINISHED && getOnGameFinished() != null)
            getOnGameFinished().accept(this);
        this.state = state;
    }

    public void setOnGameStarted(@Nonnull Consumer<Game> onGameStarted) {
        this.onGameStarted = getOnGameStarted() != null ?
                getOnGameStarted().andThen(onGameStarted) : onGameStarted;
    }

    @Nullable
    public Consumer<Game> getOnGameStarted() {
        return onGameStarted;
    }

    public void setOnGameFinished(@Nonnull Consumer<Game> onGameFinished) {
        this.onGameFinished = getOnGameFinished() != null ?
                getOnGameFinished().andThen(onGameFinished) : onGameFinished;
    }

    @Nullable
    public Consumer<Game> getOnGameFinished() {
        return onGameFinished;
    }
}

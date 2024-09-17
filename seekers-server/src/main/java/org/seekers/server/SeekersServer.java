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

import com.google.common.hash.Hashing;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import org.apiguardian.api.API;
import org.ini4j.Ini;
import org.seekers.core.Game;
import org.seekers.core.Player;
import org.seekers.core.Seeker;
import org.seekers.core.Vector2D;
import org.seekers.grpc.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * The {@code SeekersServer} class represents the server-side implementation of the Seekers game. It provides the server
 * functionality for hosting the game, handling client requests, and managing game state. The server uses gRPC for
 * communication with clients.
 *
 * @author Karl Zschiebsch
 * @author Supergecki
 * @see SeekersDriver
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.STABLE)
public class SeekersServer {
    private static final Logger logger = LoggerFactory.getLogger(SeekersServer.class);

    private final @Nonnull Server server; // gRPC server socket
    private final @Nonnull Function<Ini, Game> creator; // Game creator
    private final @Nonnull Ini config; // Configuration

    // Collections
    private final @Nonnull Map<String, Player> players = new HashMap<>();
    private final @Nonnull Map<String, String> commands = new HashMap<>();
    private final @Nonnull List<SeekersDriver> drivers = new ArrayList<>();
    private final @Nonnull List<Section> sections = new ArrayList<>();

    /**
     * Constructs a new {@code SeekersServer} instance for the port 7777.
     *
     * @param config  the config
     */
    @API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
    public SeekersServer(@Nonnull Ini config, @Nonnull Function<Ini, Game> creator) {
        this.server = ServerBuilder.forPort(7777).addService(new SeekersService()).build();
        this.creator = creator;
        this.config = config;

        for (var section : config.entrySet()) {
            sections.add(Section.newBuilder().setName(section.getKey()).putAllEntries(section.getValue()).build());
        }
        var section = config.get("drivers");
        if (section != null) {
            for (var entry : section.entrySet()) {
                var key = entry.getKey();
                var val = entry.getValue();
                if (key.startsWith("(") && key.endsWith(")")) {
                    for (var extension : key.substring(1, entry.getKey().length() - 1).split(",")) {
                        commands.put(extension.strip(), val);
                    }
                } else {
                    commands.put(key, val);
                }
            }
        }
    }

    /**
     * Starts the server and rotates the matching schedule of the tournament. This will start the game matches.
     *
     * @throws IOException if unable to bind
     */
    public void start() throws IOException {
        server.start();
        logger.info("Server started");
    }

    /**
     * Stops all old clients, logs the match results and closes the server.
     *
     * @throws InterruptedException if the shutdown is interrupted.
     */
    public void stop() throws InterruptedException {
        server.shutdown().awaitTermination(5L, TimeUnit.SECONDS);
        logger.info("Server shutdown");
    }

    private Game game;

    /**
     * Tries to host a single file over a language loader. If no language loader was found that can host the specified
     * file, it must be hosted manually.
     *
     * @param file the name of the file
     */
    private void findDriver(String file) {
        for (var entry : commands.entrySet()) {
            if (file.endsWith(entry.getKey())) {
                drivers.add(new SeekersDriver(file, entry.getValue()));
                return;
            }
        }
        logger.warn("Could not find loader for file {}", file);
    }

    public void playMatch(List<String> match) {
        game = creator.apply(config);
        game.setOnGameFinished(instance -> {
            for (var driver : drivers) {
                driver.close();
            }
            try {
                stop();
            } catch (InterruptedException ex) {
                logger.error("Could not stop server", ex);
                Thread.currentThread().interrupt();
            }
        });
        for (String player : match) findDriver(player);
        game.play();
    }

    /**
     * The {@code SeekersService} class handles the game-related gRPC service requests.
     *
     * @author Karl Zschiebsch
     * @since 0.1.0
     */
    @API(since = "0.1.0", status = API.Status.STABLE)
    protected class SeekersService extends SeekersGrpc.SeekersImplBase {

        /**
         * Handles the "command" request from a client. Updates the target and magnet properties of the specified
         * seeker.
         *
         * @param request          The command request.
         * @param responseObserver The response observer.
         * @apiNote Will throw {@code PERMISSION_DENIED} if the token is not valid. Commands that target seekers the
         * player does not control will be ignored. The seekers changed number only counts the number of seekers that
         * were successfully altered by the request.
         */
        @Override
        public void command(CommandRequest request, StreamObserver<CommandResponse> responseObserver) {
            Player player = players.get(request.getToken());
            if (player != null) {
                for (Command command : request.getCommandsList()) {
                    Seeker seeker = player.getSeekers().get(command.getSeekerId());
                    if (seeker != null) {
                        Vector2D target = new Vector2D(command.getTarget().getX(), command.getTarget().getY());
                        if (seeker.getMagnet() != command.getMagnet() || !seeker.getTarget().equals(target)) {
                            seeker.changeTarget(target);
                            seeker.changeMagnet(command.getMagnet());
                        }
                    }
                }
                responseObserver.onNext(CommandResponse.newBuilder()
                        .addAllCamps(Transformers.CAMP_TRANSFORMER.transformAll(game.getCamps()))
                        .addAllPlayers(Transformers.PLAYER_TRANSFORMER.transformAll(game.getPlayers()))
                        .addAllSeekers(Transformers.SEEKER_TRANSFORMER.transformAll(game.getSeekers()))
                        .addAllGoals(Transformers.GOAL_TRANSFORMER.transformAll(game.getGoals())).build());
                responseObserver.onCompleted();
            } else {
                logger.error("Player {} is not part of the game", request.getToken());
                responseObserver.onError(new StatusException(Status.PERMISSION_DENIED));
            }
        }

        /**
         * Handles the "join" request from a client. If there are open slots in the game, a new player is added and
         * assigned a token. The player details are stored in the players map along with the associated token and
         * dispatch helper.
         *
         * @param request          The join request.
         * @param responseObserver The response observer.
         * @apiNote Will throw {@code RESOURCE_EXHAUSTED} if there are no player slots available.
         */
        @Override
        public synchronized void join(JoinRequest request, StreamObserver<JoinResponse> responseObserver) {
            if (players.size() < 2) {
                try {
                    Player player = game.getPlayers().get(players.size());
                    if (request.hasName() && !request.getName().isBlank()) {
                        logger.info("Used name {}", request.getName());
                        player.setName(request.getName());
                    }
                    if (request.hasColor() && !request.getColor().isBlank()) {
                        logger.info("Used color {}", request.getColor());
                        player.setColor(request.getColor());
                    }
                    String token = Hashing.fingerprint2011().hashString("" + Math.random(),
                            Charset.defaultCharset()).toString();
                    players.put(token, player);

                    responseObserver.onNext(JoinResponse.newBuilder().setPlayerId(player.toString())
                            .setToken(token).addAllSections(sections).build());
                    responseObserver.onCompleted();
                } catch (Exception e) {
                    responseObserver.onError(e);
                    logger.warn(e.getMessage(), e);
                }
            } else {
                logger.error("Player {} tried to join game, but the game is already full", request.getName());
                responseObserver.onError(new StatusException(Status.RESOURCE_EXHAUSTED));
            }
        }

    }
}

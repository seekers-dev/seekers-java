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

import org.apiguardian.api.API;
import org.seekers.core.*;
import org.seekers.api.*;

import javax.annotation.Nonnull;

/**
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public class Transformers {
    private Transformers() {
        throw new UnsupportedOperationException();
    }

    public static final @Nonnull Transformer<Vector2D, Vector2DOuterClass.Vector2D> VECTOR2D_TRANSFORMER =
            input -> Vector2DOuterClass.Vector2D.newBuilder().setX(input.getX()).setY(input.getY()).build();

    public static final @Nonnull Transformer<Physical<?>, PhysicalOuterClass.Physical> PHYSICAL_TRANSFORMER =
            input -> PhysicalOuterClass.Physical.newBuilder()
                    .setId(input.toString())
                    .setAcceleration(VECTOR2D_TRANSFORMER.transform(input.getAcceleration()))
                    .setVelocity(VECTOR2D_TRANSFORMER.transform(input.getVelocity()))
                    .setPosition(VECTOR2D_TRANSFORMER.transform(input.getPosition())).build();

    public static final @Nonnull Transformer<Goal, GoalOuterClass.Goal> GOAL_TRANSFORMER =
            input -> GoalOuterClass.Goal.newBuilder()
                    .setCampId(input.getCapture() != null ? input.getCapture().toString() : "")
                    .setPhysical(PHYSICAL_TRANSFORMER.transform(input)).build();

    public static final @Nonnull Transformer<Seeker, SeekerOuterClass.Seeker> SEEKER_TRANSFORMER =
            input -> SeekerOuterClass.Seeker.newBuilder()
                    .setPhysical(PHYSICAL_TRANSFORMER.transform(input))
                    .setPlayerId(input.getPlayer().toString())
                    .setMagnet(input.getMagnet())
                    .setTarget(VECTOR2D_TRANSFORMER.transform(input.getTarget())).build();

    public static final @Nonnull Transformer<Camp, CampOuterClass.Camp> CAMP_TRANSFORMER =
            input -> CampOuterClass.Camp.newBuilder()
                    .setId(input.toString())
                    .setPlayerId(input.getPlayer().toString())
                    .setPosition(VECTOR2D_TRANSFORMER.transform(input.getPosition())).build();

    public static final @Nonnull Transformer<Player, PlayerOuterClass.Player> PLAYER_TRANSFORMER =
            input -> PlayerOuterClass.Player.newBuilder()
                    .setId(input.toString())
                    .setCampId(input.getCamp() != null ? input.getCamp().toString() : "")
                    .addAllSeekerIds(input.getSeekers().keySet()).build();
}

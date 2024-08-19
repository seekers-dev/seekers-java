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

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Every corresponding instance is associated with another object.
 *
 * @author Karl Zschiebsch
 * @since 0.1.0
 */
@FunctionalInterface
@API(since = "0.1.0", status = API.Status.EXPERIMENTAL)
public interface Transformer<I, O> {
    /**
     * @return the associated object
     */
    @CheckReturnValue
    @Nonnull
    O transform(@Nonnull I input);

    /**
     * Transforms all corresponding objects into the associated objects.
     *
     * @return the associated objects
     */
    @CheckReturnValue
    @Nonnull
    default Collection<O> transformAll(@Nonnull Collection<? extends I> inputs) {
        return inputs.stream().map(this::transform).collect(Collectors.toList());
    }
}

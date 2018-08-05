/*
 *  This file is part of Discord4J.
 *
 * Discord4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Discord4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This package containers classes related to the "StoreBroker" API. This can be thought of as a "Meta-Store" in that
 * it associates keys with Store objects. This pattern allows for the creation of a relational-like model that only
 * requires the backend to be aware of direct K->V relationships. This API assumes that all keys are long values.
 *
 * @see discord4j.store.broker.simple
 */
@NonNullApi
package discord4j.store.broker;

import reactor.util.annotation.NonNullApi;
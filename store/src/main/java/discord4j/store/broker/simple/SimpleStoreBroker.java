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

package discord4j.store.broker.simple;

import discord4j.store.Store;
import discord4j.store.broker.StoreBroker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SimpleStoreBroker<V extends Store<?, ?>> implements StoreBroker<V> {

    private final Function<Long, V> storeBuilder;  // Boxed since we are using a boxed map anyways
    private final Map<Long, V> cachedStores = new ConcurrentHashMap<>();

    public SimpleStoreBroker(Function<Long, V> storeBuilder) {
        this.storeBuilder = storeBuilder;
    }

    @Override
    public Mono<V> getStore(long key) {
        return Mono.defer(() -> Mono.just(cachedStores.computeIfAbsent(key, storeBuilder)));
    }

    @Override
    public Flux<V> getAllStores() {
        return Flux.defer(() -> Flux.fromIterable(cachedStores.values()));
    }

    @Override
    public Mono<Void> clearStore(long key) {
        return Mono.defer(() -> getStore(key)).flatMap(Store::deleteAll).then();
    }

    @Override
    public Mono<Void> clearAllStores() {
        return Flux.defer(() -> {
            Flux<V> allStores = getAllStores();
            cachedStores.clear();
            return allStores;
        }).flatMap(Store::deleteAll).then();
    }
}

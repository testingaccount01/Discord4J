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
import discord4j.store.broker.BiVariateStoreBroker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class SimpleBiVariateStoreBroker<V1 extends Store<?, ?>, V2 extends Store<?, ?>>
        implements BiVariateStoreBroker<V1, V2> {

    private final BiFunction<Long, Long, Tuple2<V1, V2>> storeGenerator;

    private final Map<Long, V1> primary = new ConcurrentHashMap<>();
    private final Map<Long, V2> secondary = new ConcurrentHashMap<>();

    public SimpleBiVariateStoreBroker(BiFunction<Long, Long, Tuple2<V1, V2>> storeGenerator) {
        this.storeGenerator = storeGenerator;
    }

    @Override
    public Mono<Void> ensureStorePresent(long key1, long key2) {
        return Mono.defer(() -> {
            if (primary.containsKey(key1) && secondary.containsKey(key2))
                return Mono.empty().then();
            else
                return Mono.empty();
        }).switchIfEmpty(Mono.defer(() -> {
            Tuple2<V1, V2> stores = storeGenerator.apply(key1, key2);
            primary.put(key1, stores.getT1());
            secondary.put(key2, stores.getT2());
            return Mono.empty().then();
        }));
    }

    @Override
    public Mono<V1> getPrimaryStore(long key) {
        return Mono.defer(() -> Mono.justOrEmpty(primary.get(key)));
    }

    @Override
    public Mono<V2> getSecondaryStore(long key) {
        return Mono.defer(() -> Mono.justOrEmpty(secondary.get(key)));
    }

    @Override
    public Flux<V1> getAllPrimaryStores() {
        return Flux.defer(() -> Flux.fromIterable(primary.values()));
    }

    @Override
    public Flux<V2> getAllSecondaryStores() {
        return Flux.defer(() -> Flux.fromIterable(secondary.values()));
    }

    @Override
    public Mono<Void> clearPrimaryStore(long key) {
        return getPrimaryStore(key).flatMap(Store::deleteAll).then();
    }

    @Override
    public Mono<Void> clearSecondaryStore(long key) {
        return getSecondaryStore(key).flatMap(Store::deleteAll).then();
    }

    @Override
    public Mono<Void> clearAllStores() {
        return Flux.defer(() -> Flux.fromIterable(primary.values()))
                .flatMap(Store::deleteAll)
                .concatWith(Flux.defer(() -> Flux.fromIterable(secondary.values()).flatMap(Store::deleteAll)))
                .then();
    }
}

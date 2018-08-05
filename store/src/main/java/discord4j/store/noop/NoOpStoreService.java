/*
 * This file is part of Discord4J.
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
package discord4j.store.noop;

import discord4j.store.Store;
import discord4j.store.broker.BiVariateStoreBroker;
import discord4j.store.broker.StoreBroker;
import discord4j.store.broker.simple.SimpleBiVariateStoreBroker;
import discord4j.store.broker.simple.SimpleStoreBroker;
import discord4j.store.noop.primitive.NoOpLongObjStore;
import discord4j.store.primitive.LongObjStore;
import discord4j.store.service.StoreService;
import discord4j.store.util.StoreContext;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.io.Serializable;

/**
 * Service which provides stores that do nothing. This is automatically used if no valid save services are found.
 *
 * @see NoOpStore
 * @see NoOpLongObjStore
 */
public class NoOpStoreService implements StoreService {

    @Override
    public boolean hasGenericStores() {
        return true;
    }

    @Override
    public <K extends Comparable<K>, V extends Serializable> Store<K, V> provideGenericStore(Class<K> keyClass,
                                                                                             Class<V> valueClass) {
        return new NoOpStore<>();
    }

    @Override
    public boolean hasLongObjStores() {
        return true;
    }

    @Override
    public <V extends Serializable> LongObjStore<V> provideLongObjStore(Class<V> valueClass) {
        return new NoOpLongObjStore<>();
    }

    @Override
    public <K extends Comparable<K>, V extends Serializable> StoreBroker<Store<K, V>> provideStoreBroker(Class<K> keyClass, Class<V> valueClass) {
        return new SimpleStoreBroker<>(l -> new NoOpStore<>());
    }

    @Override
    public <V extends Serializable> StoreBroker<LongObjStore<V>> provideStoreBroker(Class<V> valueClass) {
        return new SimpleStoreBroker<>(l -> new NoOpLongObjStore<>());
    }

    @Override
    public <K1 extends Comparable<K1>, K2 extends Comparable<K2>, V extends Serializable> BiVariateStoreBroker<Store<K1, V>, Store<K2, V>> provideBiVariateStoreBroker(Class<K1> keyClass1, Class<K2> keyClass2, Class<V> valueClass) {
        return new SimpleBiVariateStoreBroker<>((l1, l2) -> Tuples.of(new NoOpStore<>(), new NoOpStore<>()));
    }

    @Override
    public <V extends Serializable> BiVariateStoreBroker<LongObjStore<V>, LongObjStore<V>> provideBiVariateStoreBroker(Class<V> valueClass) {
        return new SimpleBiVariateStoreBroker<>((l1, l2) -> Tuples.of(new NoOpLongObjStore<>(), new NoOpLongObjStore<>()));
    }

    @Override
    public Mono<Void> init(StoreContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> dispose() {
        return Mono.empty();
    }
}

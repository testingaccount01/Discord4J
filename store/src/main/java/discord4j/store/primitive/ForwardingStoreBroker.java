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

package discord4j.store.primitive;

import discord4j.store.Store;
import discord4j.store.broker.StoreBroker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Simple wrapper to convert generic store brokers to handle LongObjStores.
 */
@SuppressWarnings("unchecked")
public class ForwardingStoreBroker<O extends Serializable, V extends LongObjStore<O>> implements StoreBroker<V> {

    private final StoreBroker<Store<Long, O>> broker;

    public ForwardingStoreBroker(StoreBroker<Store<Long, O>> broker) {
        this.broker = broker;
    }

    @Override
    public Mono<V> getStore(long key) {
        return broker.getStore(key).map(toForward -> (V) new ForwardingStore<>(toForward));
    }

    @Override
    public Flux<V> getAllStores() {
        return broker.getAllStores().map(toForward -> (V) new ForwardingStore<>(toForward));
    }

    @Override
    public Mono<Void> clearStore(long key) {
        return broker.clearStore(key);
    }

    @Override
    public Mono<Void> clearAllStores() {
        return broker.clearAllStores();
    }
}

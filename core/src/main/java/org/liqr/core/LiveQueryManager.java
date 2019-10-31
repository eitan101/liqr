/**
 * The MIT License
 * Copyright (c) 2019 Eitan Yarden
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.liqr.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @param <K> key
 * @param <V> value
 * @param <Q> query
 */
public class LiveQueryManager<K, V, Q> {
    final LiveStore<K, V, Q> store;

    public LiveQueryManager(LiveStore<K, V, Q> store) {
        this.store = store;
        this.store.subscribe(this::changed);
    }

    ConcurrentHashMap<Subscription<K, V>, Boolean> subs = new ConcurrentHashMap<>();

    public void publish(K key, Function<Optional<V>, Optional<V>> updater) {
        store.update(key, updater);
        // changed should be invoked automatically using the subsctibption
    }
    
    private void changed(KeyVal<K,Change<V>> change0) {
        Optional<Change<V>> change = Optional.of(change0.val);
        subs.keySet().stream().forEach(sub -> change
                .map(c -> new Change<>(c.oldVal.filter(sub.inQuery), c.newVal.filter(sub.inQuery)))
                .filter(rc -> rc.oldVal.isPresent() || rc.newVal.isPresent())
                .map(rc -> Arrays.asList(new KeyVal<>(change0.key, rc)))
                .ifPresent(sub.cb));        
    }

    /**
     *
     * @param query Query to the liveStore to calculate the initial result-set
     * @param cb Callback to be called on every change in the result-set
     * @return Subscription object. Can be used to un-subscribe.
     */
    public Object subscribe(LiveQuery<V,? extends Q> query, Consumer<Collection<KeyVal<K, Change<V>>>> cb) {
        final Subscription<K, V> sub = new Subscription<>(query.inQuery(), cb);
        subs.put(sub, true);
        cb.accept(store.values(query.staticQuery())
                .map(v -> new KeyVal<>(v.key, new Change<>(Optional.empty(), Optional.ofNullable(v.val))))
                .collect(Collectors.toList()));
        return sub;
    }
    
    public Boolean unsubscribe(Object c) {
        return subs.remove(c);
    }

    public LiveStore<K, V, Q> getStore() {
        return store;
    }

    public static <T> Function<Optional<T>, Optional<T>> overrideWith(T newVal) {
        return OptionalOld -> Optional.of(newVal);
    }
}

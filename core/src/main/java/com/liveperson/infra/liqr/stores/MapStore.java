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
package com.liveperson.infra.liqr.stores;

import com.liveperson.infra.liqr.core.Change;
import com.liveperson.infra.liqr.core.LiveQuery;
import com.liveperson.infra.liqr.core.LiveStore;
import com.liveperson.infra.liqr.core.KeyVal;
import com.liveperson.infra.liqr.core.Subscribable;
import com.liveperson.infra.liqr.stores.MapStore.MapQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MapStore<K, V> implements LiveStore<K, V, Predicate<V>>, Subscribable<KeyVal<K,Change<V>>> {
    ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();

    @Override
    public Stream<KeyVal<K, V>> values(Predicate<V> q) {
        return map.entrySet().stream()
                .filter(e->q.test(e.getValue()))
                .map(e->new KeyVal<>(e.getKey(),e.getValue()))
                .limit(200);
    }

    @Override
    public Change<V> update(K k, Function<Optional<V>,Optional<V>> updater) {
        List<V> oldValCont = new ArrayList<>(1);
        V newVal = map.compute(k, (key,oldVal)->{
            oldValCont.add(oldVal);
            return updater.apply(Optional.ofNullable(oldVal)).orElse(null);
        });
        final Change<V> change = new Change<>(oldValCont.get(0),newVal);
        notifyListeners(KeyVal.of(k, change));
        return change;
    }

    Set<Consumer<KeyVal<K, Change<V>>>> listeners = createListeners();
    @Override
    public Set<Consumer<KeyVal<K, Change<V>>>> listeners() {
        return listeners;
    }

    public static class MapQuery<V> implements LiveQuery<V, Predicate<V>>, Predicate<V>{
        Predicate<V> predicate;

        public MapQuery(Predicate<V> predicare) {
            this.predicate = predicare;
        }
        @Override
        public Predicate<V> inQuery() {
            return predicate;
        }
        
        public static <V> MapQuery<V> of(Predicate<V> p) {
            return new MapQuery<>(p);
        }
        
        @Override
        public boolean test(V t) {
            return predicate.test(t);
        }
        @Override
        public MapQuery<V> staticQuery() {
            return this;
        }
    }

}

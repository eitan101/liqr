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

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author eitanya
 * @param <K> key
 * @param <V> value
 * @param <Q> query for the specific store. Each store can have its own types. 
 * for example, h2 and others may use sql notation. Couchbase will use N1ql and so on.
 */
public interface LiveStore<K, V, Q> extends Subscribable<KeyVal<K,Change<V>>> {
    /**
     * 
     * @param key
     * @param updater gets old value and returns the new one
     * @return Change object with the old and the new values.
     */
    Change<V> update(K key, Function<Optional<V>,Optional<V>> updater);
    
    /**
     * 
     * @param query
     * @return stream of entries that matches the query
     */
    Stream<KeyVal<K,V>> values(Q query);    
}

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
package org.liqr;

import org.liqr.formatters.ChangesFormatters;
import org.liqr.stores.MapStore;
import org.liqr.core.LiveQueryManager;

import java.util.Optional;
import static java.util.concurrent.TimeUnit.SECONDS;
import org.junit.Test;
import org.liqr.core.Change;
import org.liqr.core.KeyVal;

import static org.liqr.core.LiveQueryManager.overrideWith;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BasicTest {
    @Test
    public void testBasic() {
        ArrayBlockingQueue<Collection<KeyVal<Long, Change<Long>>>> q = new ArrayBlockingQueue<>(10);
        LiveQueryManager<Long, Long, Predicate<Long>> lqm = new LiveQueryManager<>(new MapStore<>());
        lqm.subscribe(MapStore.MapQuery.of(n -> n % 13 == 0), q::add);
        assertEquals(0, poll(q).size());
        lqm.getStore().update(0l, overrideWith(13l));
        assertTrue(poll(q).contains(KeyVal.of(0l, Change.of(null, 13l))));
        lqm.getStore().update(0l, overrideWith(26l));
        assertTrue(poll(q).contains(KeyVal.of(0l, Change.of(13l, 26l))));
        lqm.getStore().update(0l, overrideWith(14l));
        assertTrue(poll(q).contains(KeyVal.of(0l, Change.of(26l, null))));
    }

    @Test
    public void testExtractNewVals() {
        ArrayBlockingQueue<Collection<KeyVal<Long, Optional<Long>>>> q = new ArrayBlockingQueue<>(10);
        LiveQueryManager<Long, Long, Predicate<Long>> lqm = new LiveQueryManager<>(new MapStore<>());
        lqm.subscribe(MapStore.MapQuery.of(n -> n % 13 == 0), ChangesFormatters.extractNewValsAnd(q::add));
        assertEquals(0, poll(q).size());
        lqm.getStore().update(0l, overrideWith(13l));
        assertTrue(poll(q).contains(KeyVal.of(0l, Optional.of(13l))));
        lqm.getStore().update(0l, overrideWith(26l));
        assertTrue(poll(q).contains(KeyVal.of(0l, Optional.of(26l))));
        lqm.getStore().update(0l, overrideWith(14l));
        assertTrue(poll(q).contains(KeyVal.<Long, Optional<Long>>of(0l, Optional.empty())));
    }

    @Test
    public void testSubscribeAfter0() {
        ArrayBlockingQueue<Collection<KeyVal<Long, Optional<Long>>>> q = new ArrayBlockingQueue<>(10);
        LiveQueryManager<Long, Long, Predicate<Long>> lqm = new LiveQueryManager<>(new MapStore<>());
        lqm.getStore().update(0l, overrideWith(13l));
        lqm.getStore().update(0l, overrideWith(26l));
        lqm.subscribe(MapStore.MapQuery.of(n -> n % 13 == 0), ChangesFormatters.extractNewValsAnd(q::add));
        assertEquals(1, poll(q).size());
    }

    @Test
    public void testSubscribeAfter1() {
        ArrayBlockingQueue<Collection<KeyVal<Long, Optional<Long>>>> q = new ArrayBlockingQueue<>(10);
        LiveQueryManager<Long, Long, Predicate<Long>> lqm = new LiveQueryManager<>(new MapStore<>());
        lqm.getStore().update(0l, overrideWith(13l));
        lqm.getStore().update(0l, overrideWith(26l));
        lqm.getStore().update(0l, overrideWith(14l));
        lqm.subscribe(MapStore.MapQuery.of(n -> n % 13 == 0), ChangesFormatters.extractNewValsAnd(q::add));
        assertEquals(0, poll(q).size());
    }

    @Test
    public void testMultiple() {
        ArrayBlockingQueue<Collection<KeyVal<Long, Optional<Long>>>> q13 = new ArrayBlockingQueue<>(10);
        ArrayBlockingQueue<Collection<KeyVal<Long, Optional<Long>>>> q14 = new ArrayBlockingQueue<>(10);
        LiveQueryManager<Long, Long, Predicate<Long>> lqm = new LiveQueryManager<>(new MapStore<>());
        lqm.subscribe(MapStore.MapQuery.of(n -> n % 13 == 0), ChangesFormatters.extractNewValsAnd(q13::add));
        lqm.subscribe(MapStore.MapQuery.of(n -> n % 14 == 0), ChangesFormatters.extractNewValsAnd(q14::add));
        assertEquals(0, poll(q13).size());
        assertEquals(0, poll(q14).size());
        lqm.getStore().update(0l, overrideWith(13l));
        assertTrue(poll(q13).contains(KeyVal.of(0l, Optional.of(13l))));
        lqm.getStore().update(0l, overrideWith(26l));
        assertTrue(poll(q13).contains(KeyVal.of(0l, Optional.of(26l))));
        lqm.getStore().update(0l, overrideWith(14l));
        assertTrue(poll(q13).contains(KeyVal.<Long, Optional<Long>>of(0l, Optional.empty())));
        assertTrue(poll(q14).contains(KeyVal.of(0l, Optional.of(14l))));
    }

    private static <T> Collection<T> poll(ArrayBlockingQueue<Collection<T>> q) {
        try {
            return q.poll(1, SECONDS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}

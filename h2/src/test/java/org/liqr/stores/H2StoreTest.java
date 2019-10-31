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
package org.liqr.stores;

import org.liqr.stores.h2.H2Datastore;
import org.liqr.stores.h2.H2Field;
import org.liqr.stores.h2.H2Query;
import org.liqr.core.Change;
import org.liqr.core.KeyVal;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;

import static org.junit.Assert.*;

public class H2StoreTest {

    @Test
    public void basic() {
        H2Datastore<Integer, H2Query> ds
                = new H2Datastore<>(JdbcConnectionPool.create("jdbc:h2:mem:test", "user", "pass"),
                        s -> s.toString(), Integer::parseInt, "default",
                        H2Field.of("mult2", "int", v -> Integer.toString(v * 2)));
        ds.init();

        ds.update("1", add(2));
        assertTrue(ds.values(H2Query.of("mult2 = :num", q -> q.bind("num", 4))).collect(toList())
                .contains(KeyVal.of("1", 2)));

        ds.update("1", add(-5));
        ds.update("2", add(-5));
        ds.update("3", add(-6));
        ds.update("3", add(-8));
        assertTrue(ds.values(H2Query.of("mult2 = -28")).collect(toList())
                .contains(KeyVal.of("3", -14)));
    }

    @Test
    public void basicSubscription() throws InterruptedException {

        H2Datastore<Integer, H2Query> ds
                = new H2Datastore<>(JdbcConnectionPool.create("jdbc:h2:mem:test", "user", "pass"),
                        s -> s.toString(), Integer::parseInt, "default2",
                        H2Field.of("mult2", "int", v -> Integer.toString(v * 2)));
        ds.init();
        ArrayBlockingQueue<KeyVal<String, Change<Integer>>> q = new ArrayBlockingQueue<>(10);

        ds.subscribe(q::add);
        ds.update("1", add(2));
        assertEquals(KeyVal.of("1", Change.of(null, 2)),q.poll(1, TimeUnit.SECONDS));
    }

    private static Function<Optional<Integer>, Optional<Integer>> add(int n) {
        return v -> Optional.of(v.orElse(0) + n);
    }
}

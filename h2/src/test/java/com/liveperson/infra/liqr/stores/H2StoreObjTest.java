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

import com.liveperson.infra.liqr.stores.h2.H2Query;
import com.liveperson.infra.liqr.stores.h2.H2Datastore;
import com.liveperson.infra.liqr.stores.h2.H2Field;
import com.liveperson.infra.liqr.stores.helpers.MyStoredObj;
import java.util.Optional;
import java.util.function.Function;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;

public class H2StoreObjTest {

    @Test
    public void basic() {
        H2Datastore<MyStoredObj, H2Query> ds
                = new H2Datastore<>(JdbcConnectionPool.create("jdbc:h2:mem:test", "user", "pass"),
                        s -> s.toString(), MyStoredObj::fromJSON, "default"
                        , H2Field.of("mult2", "int", v -> v.role.name())
                );
        ds.init();

//        ds.update("1", add(2));
//        assertTrue(ds.values(H2Query.of("mult2 = 4", null)).collect(toList())
//                .contains(KeyVal.of("1", 2)));
//
//        ds.update("1", add(-5));
//        ds.update("2", add(-5));
//        ds.update("3", add(-6));
//        ds.update("3", add(-8));
//        assertTrue(ds.values(H2Query.of("mult2 = -28", null)).collect(toList())
//                .contains(KeyVal.of("3", -14)));
    }

    private static Function<Optional<Integer>, Optional<Integer>> add(int n) {
        return v -> Optional.of(v.orElse(0) + n);
    }
}

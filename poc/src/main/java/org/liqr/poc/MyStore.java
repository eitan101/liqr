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
package org.liqr.poc;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.liqr.core.LiveQuery;
import org.liqr.poc.MyObj;
import org.liqr.stores.h2.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.Handle;

public class MyStore extends H2Datastore<MyObj, H2Query> {
    public MyStore(JdbcConnectionPool jcp,MetricRegistry metrics) {
        super(jcp,(o) -> o.toJSON(), MyObj::fromJSON, "default", H2Field.of("due", "bigint", (v) -> v.due.toString()));
        metrics.register("db_total", (Gauge<Long>) this::total);
    }

    public static class BetweenDates {
        public static LiveQuery<MyObj, H2Query> of(Long min, Long max) {
            return LiveQuery.of((obj) -> obj.due > min && obj.due <= max, 
                    H2Query.of(String.format("due > %d and due <= %d order by due limit 200", min, max)));
        }

        public static LiveQuery<MyObj, H2Query> of(LocalDateTime min, LocalDateTime max) {
            return BetweenDates.of(min.toEpochSecond(ZoneOffset.UTC), max.toEpochSecond(ZoneOffset.UTC));
        }
    }
    
    /**
     * Pagination query helper.
     *
     * @param minDue
     * @param pageSize
     * @return max value of due field when starting from minDue given pageSize
     */
    public LocalDateTime maxDue(LocalDateTime minDue, Integer pageSize) {
        try (final Handle h = dbi.open()) {
            return // get first as long
                    // to UTC
                    h.createQuery(String.format("select max(due) from (select due from %s where due > %d order by due limit %d)", tableName, minDue.toEpochSecond(ZoneOffset.UTC), pageSize)).map((idx, rs, ctx) -> rs.getLong(1)) // get first as long
                    .list().stream().findFirst().map((java.lang.Long l) -> LocalDateTime.ofEpochSecond(l, 0, ZoneOffset.UTC)) // to UTC
                    .orElse(LocalDateTime.MIN);
        }
    }

}

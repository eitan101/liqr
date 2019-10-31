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
package org.liqr.stores.h2;

import java.util.Map;
import java.util.function.Function;
import org.skife.jdbi.v2.Query;

public class H2Query {
    final String whereClause;
    final Function<Query<Map<String, Object>>, Query<Map<String, Object>>> bindings;

    H2Query(String whereClause, Function<Query<Map<String, Object>>, Query<Map<String, Object>>> bindings) {
        this.whereClause = whereClause;
        this.bindings = bindings;
    }

    public static H2Query of(String whereClause) {
        return new H2Query(whereClause, q -> q);
    }

    public static H2Query of(String whereClause, Function<Query<Map<String, Object>>, Query<Map<String, Object>>> binds) {
        return new H2Query(whereClause, binds);
    }
}

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
package com.liveperson.infra.liqr.core;

import java.util.function.Predicate;

/**
 * Should be used by each {@link LiveStore} to model its own queries. Each query
 * must give a way to test live update to see if it matches the query.
 *
 * @param <V> value
 * @param <Q>
 */
public interface LiveQuery<V, Q> {
    /**
     * @return A predicate that returns true for value that matches the query.
     */
    Predicate<V> inQuery();

    Q staticQuery();

    public static <V, Q> LiveQuery<V, Q> of(Predicate<V> p, Q q) {
        return new LiveQuery<V, Q>() {
            @Override
            public Predicate<V> inQuery() {
                return p;
            }
            @Override
            public Q staticQuery() {
                return q;
            }
        };
    }
}

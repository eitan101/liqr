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
package com.liveperson.infra.liqr.utils;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GeneralUtils {
   
    @FunctionalInterface
    public interface FuncitonCheckException<U,V> {
        V apply(U u) throws Exception;
    }

    public static <U, V> Function<U, V> rethrow(FuncitonCheckException<U, V> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    @FunctionalInterface
    public interface PredicateCheckException<U> {
        Boolean test(U u) throws Exception;
    }

    @FunctionalInterface
    public interface SupplierCheckException<U> {
        U get() throws Exception;
    }

    public static <U> Predicate<U> rethrowP(PredicateCheckException<U> p) {
        return t -> {
            try {
                return p.test(t);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    public static <U> Supplier<U> rethrow(SupplierCheckException<U> p) {
        return () -> {
            try {
                return p.get();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}

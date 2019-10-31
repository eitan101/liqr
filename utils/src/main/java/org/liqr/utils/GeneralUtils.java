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
package org.liqr.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneralUtils {
    @FunctionalInterface
    public interface RunnableCheckException {
        void run() throws Exception;
    }

    public static Runnable rethrow(RunnableCheckException r) {
        return () -> {
            try {
                r.run();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    @FunctionalInterface
    public interface FuncitonCheckException<U, V> {
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
    public interface ConsumerCheckException<U> {
        void apply(U u) throws Exception;
    }

    public static <U> Consumer<U> rethrowC(ConsumerCheckException<U> f) {
        return t -> {
            try {
                f.apply(t);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    @FunctionalInterface
    public interface BiConsumerCheckException<T,U> {
        void apply(T t,U u) throws Exception;
    }

    public static <U,V> BiConsumer<U,V> rethrowC(BiConsumerCheckException<U,V> f) {
        return (U t, V u) -> {
            try {
                f.apply(t, u);
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

    public static <T> Function<T, T> original(Consumer<T> f) {
        return t -> {
            f.accept(t);
            return t;
        };
    }

}

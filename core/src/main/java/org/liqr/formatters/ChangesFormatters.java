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
package org.liqr.formatters;

import org.liqr.core.KeyVal;
import org.liqr.core.Change;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;

public class ChangesFormatters {
    public static <K, V> Consumer<Collection<KeyVal<K, Change<V>>>> maskChanges(Function<V, V> mask, Predicate<Change<V>> diff, Consumer<Collection<KeyVal<K, Change<V>>>> cb) {
        return changes -> Optional.of(changes.stream()
                .map(maskEntry(mask))
                .filter(me -> diff.test(me.val))
                .collect(toList()))
                .filter(l -> !l.isEmpty()).ifPresent(cb);
    }

    public static <K, V> Consumer<Collection<KeyVal<K, Change<V>>>> extractNewValsAnd(Consumer<Collection<KeyVal<K, Optional<V>>>> cb) {
        return changes -> Optional.of(changes.stream()
                .map(me -> new KeyVal<>(me.key, me.val.newVal))
                .collect(toList())).ifPresent(cb);
    }

    protected static <K, V> Function<KeyVal<K, Change<V>>, KeyVal<K, Change<V>>> maskEntry(Function<V, V> mask) {
        return entry -> new KeyVal<>(entry.key, maskChange(mask).apply(entry.val));
    }

    protected static <V> Function<Change<V>, Change<V>> maskChange(Function<V, V> mask) {
        return change -> new Change<>(change.oldVal.map(mask), change.newVal.map(mask));
    }

}

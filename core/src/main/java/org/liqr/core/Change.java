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

import java.util.Objects;
import java.util.Optional;

public class Change<V> {
    public Optional<V> oldVal;
    public Optional<V> newVal;

    public Change(V oldVal, V newVal) {
        this(Optional.ofNullable(oldVal), Optional.ofNullable(newVal));
    }

    public Change(Optional<V> oldVal, Optional<V> newVal) {
        this.oldVal = oldVal;
        this.newVal = newVal;
    }
    
    @Override
    public String toString() {
        return "Change{" + "oldVal=" + oldVal + ", newVal=" + newVal + '}';
    } 
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.oldVal);
        hash = 47 * hash + Objects.hashCode(this.newVal);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Change<?> other = (Change<?>) obj;
        if (!Objects.equals(this.oldVal, other.oldVal))
            return false;
        if (!Objects.equals(this.newVal, other.newVal))
            return false;
        return true;
    }
    
    public static <V> Change<V> of(V old, V newV) {
        return new Change<>(old,newV);
    }
}

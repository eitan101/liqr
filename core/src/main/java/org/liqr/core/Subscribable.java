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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 *
 * @param <T> The type of the events that can be subscribed
 */
public interface Subscribable<T> {
    Set<Consumer<T>> listeners();

    default Set<Consumer<T>> createListeners(){
        return ConcurrentHashMap.newKeySet();
    };
    
    default public Consumer<T> subscribe(Consumer<T> listener) {
        listeners().add(listener);
        return listener;
    }
    
    default public void unsubscribe(Consumer<T> listener) {
        listeners().remove(listener);
    }
    
    default T notifyListeners(T t) {
        listeners().forEach(listener->listener.accept(t));
        return t;
    }
}

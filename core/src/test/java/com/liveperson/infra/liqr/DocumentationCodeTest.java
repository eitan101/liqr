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
package com.liveperson.infra.liqr;

import com.liveperson.infra.liqr.core.LiveQueryManager;
import static com.liveperson.infra.liqr.core.LiveQueryManager.overrideWith;
import com.liveperson.infra.liqr.stores.MapStore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.function.Predicate;
import org.junit.Ignore;
import org.junit.Test;

public class DocumentationCodeTest {

    @Test
    @Ignore
    public void main() throws InterruptedException {
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        LiveQueryManager<Long, Long, Predicate<Long>> lqm = new LiveQueryManager<>(new MapStore<>());

        lqm.subscribe(MapStore.MapQuery.of(n -> n % 13 == 0), System.out::println);

        // publish random event every 500 ms
        exec.scheduleAtFixedRate(
                () -> lqm.getStore().update(
                        ThreadLocalRandom.current().nextLong(100L), // random key
                        overrideWith(ThreadLocalRandom.current().nextLong(1000L))), // random value
                0, 5, MILLISECONDS); // fires every Xms

        Thread.sleep(500);
        exec.shutdown();

        /*
output example:        
[]
[KeyVal{key=68, val=Change{oldVal=Optional.empty, newVal=Optional[65]}}]
[KeyVal{key=24, val=Change{oldVal=Optional.empty, newVal=Optional[806]}}]
[KeyVal{key=47, val=Change{oldVal=Optional.empty, newVal=Optional[26]}}]
[KeyVal{key=47, val=Change{oldVal=Optional[26], newVal=Optional.empty}}]
[KeyVal{key=14, val=Change{oldVal=Optional.empty, newVal=Optional[91]}}]
[KeyVal{key=56, val=Change{oldVal=Optional.empty, newVal=Optional[169]}}]
[KeyVal{key=33, val=Change{oldVal=Optional.empty, newVal=Optional[676]}}]
[KeyVal{key=68, val=Change{oldVal=Optional[65], newVal=Optional.empty}}]        
         */
    }
}

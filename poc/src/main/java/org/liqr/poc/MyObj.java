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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import static org.liqr.utils.GeneralUtils.rethrow;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class    MyObj {
    public final String name;
    public final Integer age;
    public final Role role;
    public final Long due;

    public static MyObj random() {
        return new MyObj(
                names.get(ThreadLocalRandom.current().nextInt(names.size())),
                ThreadLocalRandom.current().nextInt(120),
                ROLES.get(ThreadLocalRandom.current().nextInt(ROLES.size())),
                LocalDateTime.now().plusMinutes(ThreadLocalRandom.current().nextLong(14400)).toEpochSecond(ZoneOffset.UTC));
    }
    
    public static enum Role {
        A, B, C, D, E, F, G
    }
    public MyObj() {
        this(null, null, null, null);
    }
    public MyObj(String name, Integer age, Role role, Long due) {
        this.name = name;
        this.age = age;
        this.role = role;
        this.due = due;
    }
    static List<String> names = Arrays.asList("Alice", "Bob", "Carly", "Dave", "Eve", "Frank", "Gil");
    private static final List<Role> ROLES = Arrays.asList(Role.values());

    public static ObjectMapper OM = new ObjectMapper().registerModules(new Jdk8Module());
    public String toJSON() {
        return rethrow(() -> OM.writeValueAsString(this)).get();
    }

    public static MyObj fromJSON(String s) {
        return rethrow(() -> OM.readValue(s, MyObj.class)).get();
    }
}

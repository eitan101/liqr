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
package com.liveperson.infra.liqr.stores.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import static com.liveperson.infra.liqr.utils.GeneralUtils.rethrow;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import static com.liveperson.infra.liqr.utils.GeneralUtils.rethrow;
import static com.liveperson.infra.liqr.utils.GeneralUtils.rethrow;
import static com.liveperson.infra.liqr.utils.GeneralUtils.rethrow;

public class MyStoredObj {

    public final String id;
    public final String name;
    public final Role role;
    public final Long time;

    public static enum Role {
        GK, CB, LB, FB, LWB, RWB, SW, DM, CM, AM, LW, RW, CD, WF
    }

    public MyStoredObj(String id, String name, Role role, Long time) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.time = time;
    }

    public static MyStoredObj random() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return new MyStoredObj(UUID.randomUUID().toString(),
                nextString(rnd, 10), nextEnum(rnd, Role.values()), rnd.nextLong(Long.MAX_VALUE));
    }

    private static <T> T nextEnum(ThreadLocalRandom rnd, T[] values) {
        return values[rnd.nextInt(values.length)];
    }

    private static String nextString(ThreadLocalRandom rnd, int len) {
        byte[] ba = new byte[len];
        rnd.nextBytes(ba);
        return Base64.getEncoder().encodeToString(ba);
    }
    @Override
    public String toString() {
        return "MyStoredObj{" + "ObjID=" + id + ", Name=" + name + ", role=" + role + ", time=" + time + '}';
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(random().toJSON());
        }
    }
  public static ObjectMapper OM = new ObjectMapper().registerModules(new Jdk8Module());
    public String toJSON() {
        return rethrow(() -> OM.writeValueAsString(this)).get();
    }

    public static MyStoredObj fromJSON(String s) {
        return rethrow(() -> OM.readValue(s, MyStoredObj.class)).get();
    }
}

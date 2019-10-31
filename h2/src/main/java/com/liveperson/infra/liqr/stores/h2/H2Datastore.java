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
package com.liveperson.infra.liqr.stores.h2;

import com.liveperson.infra.liqr.core.Change;
import com.liveperson.infra.liqr.core.KeyVal;
import com.liveperson.infra.liqr.core.LiveStore;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import static java.lang.String.format;
import java.util.Set;
import java.util.function.Consumer;
import static java.lang.String.format;
import static java.lang.String.format;
import static java.lang.String.format;

/**
 *
 * @param <V> the type of the document
 * @param <Q> the type of the query
 */
public class H2Datastore<V, Q extends H2Query> implements LiveStore<String, V, Q> {
    protected DBI dbi;
    final Function<V, String> obj2Str;
    final Function<String, V> str2Obj;
    final Collection<H2Field<V>> indexes;
    final Collection<H2Field<V>> builtIn;
    final private String qmarks;
    protected final String tableName;

    public H2Datastore(JdbcConnectionPool jcp, Function<V, String> obj2Str, Function<String, V> str2Obj, String tableName) {
        this(jcp, obj2Str, str2Obj, tableName, new H2Field[0]);
    }

    /**
     *
     * @param jcp
     * @param obj2Str serialization method from the document type to string
     * @param str2Obj desserialization method from string type to document
     * @param tableName table name in the h2 db
     * @param indexes varargs of indexes. The store will extract the indexed
     * value to separate indexed column
     */
    public H2Datastore(JdbcConnectionPool jcp, Function<V, String> obj2Str, Function<String, V> str2Obj, String tableName, H2Field<V>... indexes) {
        this.obj2Str = obj2Str;
        this.str2Obj = str2Obj;
        this.indexes = Arrays.asList(indexes);
        this.builtIn = Arrays.asList(
                H2Field.of("id", "varchar(64) primary key", null),
                H2Field.of("doc", "CLOB", null));
        this.qmarks = IntStream.range(0, builtIn.size() + this.indexes.size()).mapToObj(v -> "?").collect(Collectors.joining(","));
        this.tableName = tableName;
        this.dbi = new DBI(jcp);
    }

    /**
     * Initiates the store using the given connection pool
     *
     */
    public void init() {
        try (Handle h = dbi.open()) {
            h.execute(format("create table if not exists %s (%s)", tableName,
                    Stream.concat(builtIn.stream(), indexes.stream())
                    .map(f -> f.label + " " + f.sqlType)
                    .collect(Collectors.joining(", "))));
            indexes.stream().forEach(idx
                    -> h.execute(format("CREATE INDEX if not exists %s ON %s(%s)", idx.label + "_idx", tableName, idx.label)));
        }
    }

    /**
     * Drop the table if exist. Can be called only after init.
     */
    public void drop() {
        try (Handle h = dbi.open()) {
            h.execute(format("drop table if exists %s", tableName));
        }
    }

    /**
     *
     * @return the total amount of records in the store.
     */
    public Long total() {
        try (Handle h = dbi.open()) {
            return h.createQuery(format("select count(id) from %s", tableName))
                    .map((idx, rs, ctx) -> rs.getLong(1))
                    .list().stream().findFirst().orElse(0L);
        }
    }

    @Override
    public Change<V> update(String key, Function<Optional<V>, Optional<V>> updater) {
        try (Handle h = dbi.open()) {
            h.begin();
            Optional<V> oldVal = h.createQuery(format("select doc from %s where id = :id", tableName)).bind("id", key)
                    .map((idx, rs, ctx) -> {
                        final String s = rs.getString("doc");
                        return str2Obj.apply(s);
                    })
                    .list().stream().findFirst();
            Optional<V> newVal = updater.apply(oldVal);
            if (newVal.isPresent()) {
                final Object[] values = Stream.concat(Stream.of(key, newVal.map(obj2Str).get()),
                        indexes.stream().map(idx -> newVal.map(idx.extractor).get())).toArray();

                h.update(format("MERGE INTO %s KEY(id) VALUES(%s)", tableName, qmarks), values);
                h.commit();
            } else {
                h.update(format("DELETE FROM %s WHERE id = '%s'", tableName, key));
                h.commit();
            }
            final Change<V> change = new Change<>(oldVal, newVal);
            notifyListeners(KeyVal.of(key, change));
            return change;
        }
    }

    @Override
    public Stream<KeyVal<String, V>> values(Q query) {
        try (Handle h = dbi.open()) {
            return Optional.of(h.createQuery(format("select id,doc from %s where %s", tableName, query.whereClause)))
                    .map(query.bindings)
                    .map(q -> q.map((idx, rs, ctx) -> KeyVal.of(rs.getString("id"), str2Obj.apply(rs.getString("doc"))))
                            .list().stream()).orElse(Stream.empty());
        }
    }

    Set<Consumer<KeyVal<String, Change<V>>>> listeners = createListeners();
    @Override
    public Set<Consumer<KeyVal<String, Change<V>>>> listeners() {
        return listeners;
    }
}

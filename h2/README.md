# liqr-h2 - h2 adapter for live queries


## Getting started

In Maven:

```xml
<dependency>
    <groupId>com.liveperson.infra</groupId>
    <artifactId>liqr-h2</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

Here is a simple example that initiates h2 datastore.

```java
public void basic() {
    H2Datastore<Integer, H2Query<Integer>> ds
            = new H2Datastore<>(s -> s.toString(), Integer::parseInt, "default",
                    H2Field.of("mult2", "int", v -> Integer.toString(v * 2)));
    ds.init(JdbcConnectionPool.create("jdbc:h2:mem:test", "user", "pass"));

    ds.update("1", add(2));
    assertTrue(ds.values(H2Query.of("mult2 = 4", null)).collect(toList())
            .contains(KeyVal.of("1", 2)));

    ds.update("1", add(-5));
    ds.update("2", add(-5));
    ds.update("3", add(-6));
    ds.update("3", add(-8));
    assertTrue(ds.values(H2Query.of("mult2 = -28", null)).collect(toList())
            .contains(KeyVal.of("3", -14)));
}

private static Function<Optional<Integer>, Optional<Integer>> add(int n) {
    return v -> Optional.of(v.orElse(0) + n);
}
```


## Major capabilites

The h2 generic stores gives you a way to define your key/value store, indexing it with a few indexes,
and query the value or the indexes using sql queries.


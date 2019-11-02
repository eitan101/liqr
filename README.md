# liqr - LiveQuery made easy
[![Build Status](https://img.shields.io/badge/build--status-TODO-blue.svg)]()
[![Manual maven central](https://img.shields.io/badge/maven--central-not--yet-blue.svg)]()

liqr (prononced as liqueur), is a lightweight framwork for running live queries.

## What is a live query
In a regular query the client sends the query and gets the results. Once the client wants to get updated results it needs to send the query again, gets the whole results list and update its model.

In a live query the client sends the query, get inital results, and then keeps getting updates to the results list. The updates can be either insertion, update or deletion of records from the results list.

## Getting started

You can build and run the test using docker-maven:
```
alias build-machine='docker run --rm -it -v $HOME/.m2repo:/root/.m2/repository -v $PWD:/my -p 8080:8080 -w /my maven:3.6.2-jdk-11-slim'
build-machine mvn test
```


The following producer publishes random long key-values. The consumer subscribes to entries with value that is divisble by 13.

```java
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
}
```

Here is an example to the output:
```txt
[]
[KeyVal{key=68, val=Change{oldVal=Optional.empty, newVal=Optional[65]}}]
[KeyVal{key=24, val=Change{oldVal=Optional.empty, newVal=Optional[806]}}]
[KeyVal{key=47, val=Change{oldVal=Optional.empty, newVal=Optional[26]}}]
[KeyVal{key=47, val=Change{oldVal=Optional[26], newVal=Optional.empty}}]
[KeyVal{key=14, val=Change{oldVal=Optional.empty, newVal=Optional[91]}}]
[KeyVal{key=56, val=Change{oldVal=Optional.empty, newVal=Optional[169]}}]
[KeyVal{key=33, val=Change{oldVal=Optional.empty, newVal=Optional[676]}}]
[KeyVal{key=68, val=Change{oldVal=Optional[65], newVal=Optional.empty}}]        
```
The first line represent the initial result-set, while the rest of the lines represents the changes. Changes in which the oldVal is empty stands for insertions and changes in which the newVal is empty stands for deletions.

## Architecture

* Live store - query, livequery, updater, H2Store, MapStore
* Change formatter
* Live Query Manager

## Messages Order and Thread Safety
The changes are sent to the subscriber using the thread of the publisher while the inital resultset is sent using the subscriber thread. Thus, the initial resultset can be sent out of order. Also, when multiple publisher thread are used, some changes can be sent also out of order.

In order to handle this cases, the framework should support in the future versioning of store records, ignoring stale updates. Till then, the logic of the updates should handle ignoring those updates.

## Subscription

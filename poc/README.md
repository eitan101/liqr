# liqr poc
This POC how to build a web app that implement a live query.
The POC creates a stream of random entity changes and applies it to the data store.
The client subscribes to a query that contains entities between dates.

## Building it
run:

```sh
mvn package
```

## Running it

run:

```sh
java -jar poc/target/liqr-poc-0.1-SNAPSHOT.jar server
```

That's all.

You can then access the REST api. For example in order to see all objects with timestamp in the next 2 minutes:
http://localhost:8080/query?val=2

[//]: # (or using simple UI using rest:)
[//]: # (http://192.168.99.100:8080/static/demo.htm)

You can also take a look at the websocket version of LiveQuery:
http://localhost:8080/static/demoWs.htm



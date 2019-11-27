# liqr poc
This POC how to build a web app that implement a live query.
The POC creates a stream of random entity changes and applies it to the data store.
The client subscribes to a query that contains entities between dates.

## Building it
run:

```sh
alias build-machine='docker run --rm -it -v $HOME/.m2repo:/root/.m2/repository -v $PWD:/my -p 8080:8080 -w /my maven:3.6.2-jdk-11-slim'
build-machine mvn package
```

## Running it

run:

```sh
build-machine java -jar poc/target/liqr-poc-0.1-SNAPSHOT.jar server
```

That's all.

You can then access the REST api. For example in order to see all objects with timestamp in the next 200 minutes:
http://localhost:8080/query?val=200

Or see polling UI in: http://localhost:8080/static/demo.htm

[//]: # (or using simple UI using rest:)
[//]: # (http://192.168.99.100:8080/static/demo.htm)

You can also take a look at the websocket version of LiveQuery:
http://localhost:8080/static/demoWs.htm

You can see a screencast of this demo in https://www.youtube.com/watch?v=fV0-Jq9WMWY

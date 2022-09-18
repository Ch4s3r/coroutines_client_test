# coroutines_client_test

Explore how to make 1000+ simultaneos calls that are waiting with one thread.

## Start the wiremock instance

```shell
docker run -it --rm -d -p 8080:8080 --name wiremock -v $PWD:/home/wiremock wiremock/wiremock:2.34.0 --async-response-enabled=true --async-response-threads=1000 --no-request-journal
```

## Test the wiremock endpoint

```shell
curl http://localhost:8080/hello
```
# mp-jwt-moviefun
Microprofile JWT example based on the Movie Fun app


# Start the gateway

```
docker run -it --name tag -e LICENSE=accept -e CASSANDRA_EMBEDDED=true -p 8080:8080 tomitribe/tribestream-api-gateway:latest
```

# Start the movie fun app

It will create the additional data at startup

```
mvn clean install -DskipTests tomee:run
```

It should start on port 8181 so it does not clash with the Gateway

# convert a private key from traditional format to pkcs#8 format.

openssl pkcs8 -topk8 -inform pem -in src/main/resources/privateKey.pem -outform pem -nocrypt -out src/main/resources/privateKey-pkcs8.pem

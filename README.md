# gateways-api

gateways-api is a private dockerized microservice that connects with the gateway DB 
to allow CRUD operations on the gateways and their devices.

## Build 

To build the application run the following command from the repository root

```bash
mvn
```

to build without running the unit and integration tests

```bash
mvn -DskipTests -DskipITs
```

# Run Locally

To run the application with test data run the following script from the repository root

```bash
bash start-up.sh
```
To just run the gateway database run the following script from the repository root

```bash
bash start-db.sh
```

# Debugging Locally

The gateways-api docker container that you started in the `Run Locally` step exposes port `5005` for remote debugging.


# Documentation

Swagger documentation can be found here :
```bash
http://localhost:8080/gateways-api/swagger-ui.html
```

Swagger endpoint's definitions
```bash
http://localhost:8080/gateways-api/api-docs/
```
to generate the open-api docs
```bash
http://localhost:8080/gateways-api/api-docs.yaml
```
all open-Api links are secured with basicAuth, look for `gateways.security.name` and `gateways.security.password` in **application.yaml** in ./gateways-api/src/main/resources.

you can import the generated open-api document into postman collection to directly call the gateway endpoints,
the file `local-api-docs.yaml` has the open-api file that you can use in your collection


## Security

basic authentication secures The callback endpoint. Username and password set by the following properties:

```yaml
endpoint:
  security:
    user:
      name: ${gateways.security.name}
      password: ${gateways.security.password}
      role: ${gateways.security.role}
      management-context: ${management.server.base-path}
```
check **application.yaml** in ./gateways-api/src/main/resources



## Logging incoming requests & responses

All incoming rest requests/responses are saved in http_trace table in the gateway DB, to load the last 100 requests call this endpoint:

```bash
http://localhost:8080/gateways-api/actuator/httptrace
```


# Testing

## Automated
To run all tests, execute the following command from the repository root

```bash
mvn
```

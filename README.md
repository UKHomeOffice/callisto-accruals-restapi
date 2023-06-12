# Callisto Accruals REST API

## Building project

To build the project, you will need to install Maven. You will also need JDK 17.

```sh
$ mvn clean install
```

## Integration Tests

AccrualsControllerIntegrationTest.java is using a postgres test container. When running locally this
requires docker to be running to allow access to the docker daemon.
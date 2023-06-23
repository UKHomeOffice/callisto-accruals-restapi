# Callisto Accruals REST API

## Building project

To build the project, you will need to install Maven. You will also need JDK 17.

### Github Package dependencies
In order to pull in github package dependencies you will need a Github Personal Access Token.
This token will need the minimum of 'packages:read' permissions.

Assign the value of the token to an environment variable with the name GITHUB_TOKEN

Then run the following to build the project

```sh
$ mvn -s ./acccruals_settings.xml clean install
```

## Integration Tests

AccrualsControllerIntegrationTest.java is using a postgres test container. When running locally this
requires docker to be running to allow access to the docker daemon.
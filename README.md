# Callisto Accruals REST API

## Building project

To build the project, you will need to install Maven. You will also need JDK 17.

### Github Package dependencies
In order to pull in Github package dependencies you will need a Github Personal Access Token.
This token will need the minimum of 'packages:read' permissions.

Update your .m2/settings.xml file to contain the <servers><server> tags like timecard_settings.xml
The token will need to live within your local .m2/settings.xml file as the password

Then run the following to build the project

```sh
$ mvn clean install
```

## Integration Tests

AccrualsControllerIntegrationTest.java is using a postgres test container. When running locally this
requires docker to be running to allow access to the docker daemon.
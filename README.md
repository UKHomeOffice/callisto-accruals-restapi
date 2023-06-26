# Callisto Accruals REST API

Accruals-restapi service is a part of Callisto project (Check section [All Callisto Repositories](#headAllRepo) for links to all Callisto repositories)

## 1. Building project

To build the project, you will need to install Maven. You will also need JDK 17.

### Github Package dependencies
In order to pull in Github package dependencies you will need a Github Personal Access Token.
This token will need the minimum of 'packages:read' permissions.

Update your .m2/settings.xml file to contain the <servers><server> tags like timecard_settings.xml
The token will need to live within your local .m2/settings.xml file as the password

For more info see:
[https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

Then run the following to build the project

```sh
$ mvn clean install
```

## 2. Running project locally

#### Ports

- Standalone service runs on port 9090.
- LocalDev solution runs on port 50200.
- Java Debugger runs on port 5005.

  Check `docker-compose.yml` for any port mappings.

#### Database

- It requires a running instance of the Postgres database (For ports, username and password, please check docker-compose.yml)
- Check `<project_root>/src/main/resources/application.properties` for all required environment variables.
- The project uses Liquibase for db migrations. All required files are in `<project_root>/db` directory. Please, also check `docker-compose.yml`.
- In case of having issues with accruals schema, create schema manually or use LocalDev solution to create schema (check section [3](#headLocalDev) )
- For more information visit https://github.com/UKHomeOffice/callisto-timecard-restapi section `Create database`

## <a name="headLocalDev"></a> 3. Running project locally as part of LocalDev environment.

1. Download LocalDev repository from https://github.com/UKHomeOffice/callisto-localdev and run it locally as described in Scenario 1.

2. From the LocalDev project root, stop Accruals-restapi service by running `docker compose stop accruals-restapi` command.

3. Pull Accruals-restapi repository and from its root directory, run command `docker compose up -d`

After successful start, you should be able to work with Accruals-restapi code and all changes will be reflected within LocalDev environment.

### 3.1 Attaching Debugger

The service can also be debugged. To do so in a JetBrains IDE, a debugger has to be added (Run -> Edit Configurations.. -> Add Remote JVM Debug), with the port matching the external port mapped in the docker compose file of the service to debug. For example, that would be 5005 for this service.

## 4. Devtools Hot Deployment in local environment

Devtools allows you to reload the application after making any changes to the project files.
However, it may need stage of building project manually (InteliJ IDEA: Build/Build Project)
or IntelliJ IDEA has 2 properties that will allow you to execute `Build Project` automatically. To enable that :

1.  Go to `Preferences/Build,Execution,Deployment/Compiler` and select option
    `Build project automatically`
2.  [Optional] Go to `Preferences/Advanced Settings` and select `Allow auto-make to start even if developed application is currently running`

## 5. Integration Tests

AccrualsControllerIntegrationTest.java is using a postgres test container. When running locally this
requires docker to be running to allow access to the docker daemon.

## <a name="headAllRepo"></a> 6. All Callisto repositories

- https://github.com/UKHomeOffice/callisto-accruals-restapi
- https://github.com/UKHomeOffice/callisto-balance-calculator
- https://github.com/UKHomeOffice/callisto-person-restapi
- https://github.com/UKHomeOffice/callisto-timecard-restapi
- https://github.com/UKHomeOffice/callisto-accruals-person-consumer
- https://github.com/UKHomeOffice/callisto-auth-keycloak
- https://github.com/UKHomeOffice/callisto-build-github
- https://github.com/UKHomeOffice/callisto-kafka-commons
- https://github.com/UKHomeOffice/callisto-devops
- https://github.com/UKHomeOffice/callisto-docs
- https://github.com/UKHomeOffice/callisto-helm-charts
- https://github.com/UKHomeOffice/callisto-ingress-nginx
- https://github.com/UKHomeOffice/callisto-jparest
- https://github.com/UKHomeOffice/callisto-localdev
- https://github.com/UKHomeOffice/callisto-postman-collections
- https://github.com/UKHomeOffice/callisto-service-template
- https://github.com/UKHomeOffice/callisto-ui
- https://github.com/UKHomeOffice/callisto-ui-nginx

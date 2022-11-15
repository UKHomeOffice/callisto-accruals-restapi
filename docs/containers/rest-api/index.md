# Accruals REST API container

## Executive summary
In Border Force many people are on what is known as an Annualised Hours Agreement (AHA). In short each person will have a set of target hours that they need to work in order to gain an uplift in their basic pay. The Accruals container tracks how people are performing against those targets.

## What is the container for and why would you use it?
There is no direct database access in Accruals (see relevant decision here for the rationale). Instead this container controls access to Accruals data and it also makes sure that the integrity of the data is maintained. 

The container also presents some calculated resources through its API. More information on this can be found in the [rest-operations.md](./rest-operations.md) page.
 
## Dependencies
None

## Container contract
The container exposes its functionality through a set of RESTful endpoints that are documented in the [rest-endpoints.md](./rest-endpoints.md) page

## Contents

- [container-model.c4](./container-model.c4) - a C4 model of the system, used to generate images that visualise the API of the container and components within it
- [rest-endpoints.md](./rest-endpoints.md) - generated from [openapi.yaml](../../../src/main/resources/openapi.yaml) it describes the REST endpoints
- [rest-operations.md](./rest-operations.md) - some of the resources exposed by the REST API are calculated. This page list those resources and also describes how they are calculated
- [storage.md](./storage.md) - describes the database schema

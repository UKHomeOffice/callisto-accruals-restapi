# Accrual Balance maintainance

End users do not directly make changes to Accruals data. Instead the balance-calculator component responds to events produced by the TimeCard container.

There are two main activities initiated in TimeCard that Accruals is interested in

### 1. User records their time

When a user records time in their TimeCard then the Accruals container needs to respond by updating the balances with those Accrual modules that are effected by the newly recorded time.

The Accruals container receives information about recorded time as `TimeEntry` events. The events are sent asynchronously and the component makes the assumption that they are arriving in the correct order. 

### 2. User records a flexible change
TODO

For thrse scenarios there is no RESTful component. The process is triggered by the arrival of events from the TimeCard container that encapsulate `TimeEntry` or `FlexChange` resources. The resulting data created by the processing are then stored in the Accrual container's data store. 

Other feature designs will explain how that stored data is to be exposed as resources via the Accrual's RESTful API (see [Out of Scope](#out-of-scope)). 

## Flow
The process from receiving TimeCard events to updating Accrual balances can be broken down into 4 key steps - 

1. Consume event from topic
2. Identify Accrual type(s)
3. Calculate and update Accrual balances
4. Persist updated Accrual instances

### Consume event from topic

More detail on consuming eventscan be found in the relevent [blueprint](https://github.com/UKHomeOffice/callisto-docs/blob/main/blueprints/event-publishing-and-consuming.md#event-consumer). 

Topic name - `callisto-timecard`

### Identify Accrual type(s)
The data in the TimeCard events and the event type itself are used to identify which types of Accrual should be updated.

There are a number of different types of Accrual and depending on the data in the TimeCard event only some will be relevent.

For detail on how to use the event data to identify which Accrual type(s) is relevent see the [features](../features/index.md) which are broken down by Accrual type.

### Calculate and update Accrual balances
Once the Accrual types have been identified then the next step is to identify the specific Accrual instances (i.e. days). Having done that then their balances can be recaluated.

For detail on how to use the data in the TimeCard event to identify the accrual instances and how to calculate their new balances see [accrual-balance-calculation.md](./accrual-balance-calculation.md) 

### Persist updated Accrual instances
Finally, having updated the Accrual instance's balances they must be persisted to the Accruals container's datastore via a RESTful call to the `callisto-accrual-restapi` component.

**TODO** - link to the doc for the RESTFul endpoint

![](./../images/calculate-and-update-accrual-balances-detail.png)

The sequence diagram above shows a number of components within the `accrual-balance-calculator` container. These components represent the logical steps that need to be taken in order to take the data in a TimeCard event and use it to update the balance on one or more Accrual instances.

### Components
The components map to the process outlined above

#### TimeCard events consumer
Responsible for reading TimeCard events from the `callisto-timecard` topic

#### Orchestrator
Receives TimeCard events from the consumer and coordinates the updating of the balances of all relevent Accrual instances.

The Orchestrator is responsible for ensuring that all Accrual instances have been persisted which is a result of a successful application of the logical steps outlined below. 

- Successful processing - if all of the logical steps outlined below succeed then the Orchestrator must tell the TimeCard events consumer to release the TimeCard event from the topic. 

- Failed processing - if any of the logical steps outlined below fails then the Orchestrator must tell the TimeCard events consumer to hold on to the TimeCard event from the topic. At this point the Orchestrator should try to reprocesses the event. Note that the process outlined below as a series of logical steps plus the alogorthm that is referecned below are designed to be idempotent. Therefore simply reprocessing the event until success can be done indefintiluy

Since the process outlined above has a dependency on the `callisto-accrual-restapi` it is possible that there could be a persistent failure to complete the process.

The implementation should guard against this by using a combination of an [exponential backoff retry for transative errors and a circuit breaker for handling persistant failure](https://dzone.com/articles/understanding-retry-pattern-with-exponential-back)

`callisto-accrual-restapi` endpoints

- PUT /resources/accruals

#### Accrual Type identifier
This is intended to an implementtion of the [strategy pattern](https://en.wikipedia.org/wiki/Strategy_pattern) because the alogorithm for determing which Accrual type a TimeCard event should effect varies by Accrual type. On this basis it is envisaged that there will be a series of concrete implementations of an Accrual Type identifer interface and the Orchestroatr simply cylces through each asking if the TimeCard event ties to the given Accrual type that the implementation knows about.

`callisto-accrual-restapi` endpoints
Depending upon the strategy none or some of these endpoints might be used

- GET /operations/isBankHoliday
- GET /operations/isNightShift
- GET /operations/isNormalShift

#### Accrual Finder
Used to find the Accrual instances that are to be updated based on data in the TimeCard event and also the type(s) of Accrual that the Accrual Type identifier returned. More information on how to use TimeCard event data along with Accrual type data can be found in [accrual-balance-calculation.md](./accrual-balance-calculation.md). 

`callisto-accrual-restapi` endpoints

- GET /resources/accruals

#### Balance calculator
Having found the Accrual instances which are to be updated this component is responsible for calculating new balances and updating the owning Accrual instance. More information on how to use TimeCard event data to calcucualte a balance can be found in [accrual-balance-calculation.md](./accrual-balance-calculation.md).

## Considerations
- **Number of calls to `callisto-accruals-restapi`** - the Orchestrator component makes use of a number of other components some of which will call out to the RESTful endpoints exposed by `callisto-accruals-restapi`. A decision needs to be made as to whether or not to write the orachestor such that calls are batched up or sent individually. There are two classes of call for which this decision needs to be made: Accrual finder & Accrual storage. One could imagine finding or updating all Accrual instances for every relevent Accrual type all at once or batching them up by type
- **What (if any) components to run in parallel** - potentaiily the Accrual Type idnetifier stategiy implementations could be called in parallel. Similarly if the act of calcuation and storage were bounded by type then these could be wrapped up into that pareallel exection as well

## Out of scope
- Exposing Accrual resources via RESTful endpoints. This will be covered elsewhere.
- Seeding of accrual records based on an AH Agreement. This will be covered by a separate design invloving the [TAMS Agreement Adapter](https://github.com/UKHomeOffice/callisto-docs/blob/main/containers.md#tams-agreement-adapter)
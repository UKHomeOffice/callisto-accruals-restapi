# Accrual Type Identification
In the context of calculating the balance of one or more `Accrual` instances one key element is identifying the type of accruals that should be updated.

The data in the TimeCard events and the event type itself are used to identify which types of Accrual should be updated.

There are a number of different types of Accrual and depending on the data in the TimeCard event only some will be relevent. The way that the event is used to identify an accrual type varies from type to type


## Annual Target Hours
The data held in the `TimeEntry` event is used to determine whether or not the Annual target hours Accrual type should be updated.

More detail can be found in [Annual Target Hours - Count](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1497). In summary the following events should be excluded from updating the Annual Target hours module -

1. `FlexChange`
2. `TimeEntry` where `TimeEntry.timePeriodType` signifies that the entry is for an on-call event

Other than that every time entry change that is attributed to a worker contributes to their Annual Target hours accrual module. 


`callisto-accrual-restapi` endpoints
Depending upon the strategy none or some of these endpoints might be used

- GET /operations/isBankHoliday
- GET /operations/isNightShift
- GET /operations/isNormalShift
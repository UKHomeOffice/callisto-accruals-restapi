# Calculate Is Night Shift

The Accruals REST API exposes a number of resources that are calculated at query-time as opposed to being retrieved from the data store and returned to the client as-is.

This document explains which resources they are and how their various properties are calculated

## Night Shift

If an individual works for more than three hours within a 'night' period usually between 23:00 – 06:00, it is considered a single night shift.

This endpoint simply returns whether the data supplied qualifies as a night shift.

- [Is Night Shift endpoint](./../rest-endpoints.md#opIdisNightShift)
- [Storage model](./../storage.md)

### Calculation

There are a number of factors involved in whether a shift qualifies as a night shift.

* The employment_type, which for AHW workers can be SDA, AHA, AAA.
* Start and End Time of the night hour range which does vary based on the employment_type.
* The minimum number of hours that a shift MUST overlap the night hour range to qualify.

The data retrieval is relatively trivial. There are three tables to be accessed to acquire the data required. 

| Table                 | Accessed by      | Provides                |
| --------------------- | ---------------- | ----------------------- |
|                       |                  |                         |
| Person                | PersonId         | employment\_type        |
| night\_hour\_range    | employment\_type | start\_time, end\_time  |
| ahw\_type\_definition | employment\_type | min\_overlapping\_hours |
|                       |                  |                         |

These tables can also be seen in the following image:

![External Reference Data](./../images/storage-model-ref-external.png)




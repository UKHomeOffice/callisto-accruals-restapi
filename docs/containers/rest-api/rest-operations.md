# REST Operations

The Accruals REST API exposes a number of resources that are calculated at query-time as opposed to being retireved from the data store and returned to the client as-is.

This document explains which resources they are and how their various properties are calculated

## AccrualSummary
Provides an overview of a specific type of Accrual for a specific date. Pulls together data from a number of different tables in the data store.

- [AccuralSummary endpoint](./rest-endpoints.md#opIdgetAccrualSummaries)
- [Storage model](./storage.md)

### Calculation
There will be one `AccrualSummary` per accrual_type regardless of whether or not the person has bought the given Accrual type

Therefore, for each record from the [accrual_type](./storage.md#accrual_type) table create an AccrualSummary instance and populate it's properties as detailed below.

### Properties

parameters in scope when calculating properties

- [accrual_type](./storage.md#accrual_type) record (see [calculation](#calculation))
- personId (HTTP query param)
- date (HTTP query param)

#### name
- [accrual_type.name](./storage.md#accrual_type) column

#### measurementUnit
- [accrual_type.measurement_unit](./storage.md#accrual_type) column

#### personId
- [personId](./rest-endpoints.md#opIdgetAccrualSummarys) HTTP query param

#### date
- [date](./rest-endpoints.md#opIdgetAccrualSummarys) HTTP query param

#### target

```
SELECT accrual.target
  FROM accrual 
 WHERE accrual.personId = personId AND
       accrual.id = accrualTypeId AND
       accrual.date = date
```

[accrual](./storage.md#accrual) table schema

#### remainingHighPrecision

```
SELECT accrual.balance
  FROM accrual 
 WHERE accrual.personId = personId AND
       accrual.id = accrualTypeId AND
       accrual.date = date
```

[accrual](./storage.md#accrual) table schema

#### remainingLowPrecision

TODO - how to round [remainingHighPrecision](#remainingHighPrecision)

#### targetVariance
The calculation for this property varies from Accrual type to Accrual type. See [features](./features/index.md) for the various types and the associated calculation

#### agreementVariance
To calculate how much the Accrual's balance is over or under the agreed target

`agreementVariance` = [`total`](#total) - [`remainingHighPrecision`](#remaininghighprecision)

#### targetStatus
The calculation for this property varies from Accrual type to Accrual type. See [features](./features/index.md) for the various types and the associated calculation

#### total
if [totalNetOrGrossOfPH](#totalNetOrGrossOfPH) is `net_of_ph` then the number of public holiday hours needs to be retrieved and deducted from the raw total for the given Accrual type

**TODO** - this value cannot yet be populated as its calculation is dependent on 
- Accruals ref data work
- Agreement model such that Accruals can store the Accural target total

#### totalNetOrGrossOfPH
**TODO** - this value cannot yet be populated as its calculation is dependent on the person table being defined along with the RESTful endpoints used to manage it. Also need to think about how to onboard this data and where from

If the statement below returns true then the value should be `net_of_ph` otherwise it should be `gross_of_ph`

```
SELECT person.target_totals_are_net
  FROM person 
 WHERE person.id = personId AND
```

## isBankHoliday
**TODO** when ref data work has been merged

## isNormalShift
**TODO** when ref data work has been merged

## isNightShift
**TODO** when ref data work has been merged
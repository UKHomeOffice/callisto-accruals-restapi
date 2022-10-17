# User records their time
When a user records time in their TimeCard then the Accruals container needs to respond by updating the balances with those Accrual types that are effected by the newly recorded time.

**Communication of recorded time**
The Accruals container receives information about recorded time as `TimeEntry` events. The events are sent asynchronously and the container makes the assumption that they are arriving in the correct order. 

**Data models**
As mentioned above the [`TimeEntry`](https://github.com/UKHomeOffice/callisto-timecard-restapi/blob/main/docs/payload.md#timeentry) encapsulates the data that the Accruals container uses to update balances. Internally balance data is held by the [`Accrual` resource](../../payload.md#accrual)

## Updating Accrual balance
One of the key properties on an Accrual instance is the balance. Each instance represents the balance on a given date. When `TimeEntry` events are received then they are used to identify which type of `Accrual` they relate to and also to identify which date(s) are to have their balances calculated. Note that the mechanism for identiftying an Accrual based on the data in a TimeEntry is outside of the scope of this document. The business rules under the [Annual Target Hours feature](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1249) in Jira should be consulted.

A key part of an `Accrual` instance is its set of `Contribution` instances.  A contribution references a `TimeEntry` and it also records how many hours of work that `TimeEntry` contributes to the `Accrual` instance's balance. 

By combining all `Contribution` instances for a given `Accrual` (as there could be multiple `TimeEntry` events spread over a single day) Accruals can work out the balance for a given `Accrual`. This process is described in the following sections.

### How to calculate `Accural.contribution.contributedHours`
When a new `TimeEntry` is received then Accruals must recalculate effected balances. Depending on how many contiguous days the new `TimeEntry` covers and whether or not the `TimeEntry` represents an update (typically to move time around) or a deletion then the action required is different.

#### Single day
If the date portions of the `TimeEntry.actualStartTime` and `TimeEntry.actualEndTime` are for the same date then this `TimeEntry` covers a single day.

`contributedHours` = `TimeEntry.actualEndTime` - `TimeEntry.actualStartTime`

#### Overlapping day
If the date portions of the `TimeEntry.actualStartTime` and `TimeEntry.actualEndTime` are for the different dates then this `TimeEntry` covers multiple days. The assumption is that the days are contiguous and that

 typically no more than two days are spanned. 

Use the date portions of the `TimeEntry.actualStartTime` and `TimeEntry.actualEndTime` to work out which dates are covered. From there apportion the total hours covered by the `TimeEntry` across each date. For example given the following

 - actualStartTime: `2022-06-25 19:00:00` 
 - actualEndTime: `2022-06-26 06:00:00`

The totals work out to 

 - overall total is 11 hours 
 - 25 June total is 5 hours 
 - 26 June total is 6 hours

This would trigger the creation of a `Contribution` for the Accrual associated with 25 June with a `contributedHours` of 5 and a `Contribution` for the Accrual associated with 26 June with a `contributedHours` of 6 (see [Scenario 4: Create a `TimeEntry` spanning two contiguous days](#TODO))

#### Deletion
When a `TimeEntry` is deleted then it's `deleted` property will be set to true and its `version` property will have been incremented. In this case the Accrual instance that the deleted `TimeEntry` should be associated with can be found by one of the [single](#single-day) and [overlapping](overlaping-day) approaches above. 

Once the `Accrual` instance has been found then a new `Contribution` must be created that references the newly received `TimeEntry` and sets `contributedHours` to zero.

#### Move
Sometime a `TimeEntry` is modified. In this case the `TimeEntry.version` will be incremented. It is entirely possible that a `TimeEntry` could be moved such that it no longer overlaps with the date that it did previously.

When a `TimeEntry` is received as well as matching Accruals by date as with the [single](#single-day) and [overlapping](overlaping-day) cases above the system must also look for any Accruals with `Contribution` instances containing a `TimeEntry` with the same `id` as the newly received `TimeEntry`. If any are found where the date range covered by the new version of  `TimeEntry` no longer overlaps with the `Accrual.date` then a new `Contribution` must be created that references the newly received `TimeEntry` and sets `contributedHours` to zero.

### How to calculate `Accrual.balance`

**Assumption** - the type of Accrual to calculate a balance for has is already known

To calculate balance (`day's balance`) for a given day `day` for accrual type `type` - 

1. Get the Accrual for the `day` in question

 `day's Accrual` = select Accrual where Accrual.date = `day` AND Accrual.type = `type`
 
2. Get the balance for the previous day 

`previous day's balance` = select Accrual.balance where Accrual.date = `(day - 1)` AND Accrual.type = `type`

3. Get the contributions containing unique (by id) `TimeEntry` instances with the highest version for the given `TimeEntry.id`. This get's the most recent `TimeEntry` which should reflect what the user entered on their timecard

`day's contributions[]` = select contribution, max(contribution.timeentry.version) from `day's Accrual` group by contribution.timeentey.id

4. Add up the contributedHours for each of the `days contributions`

`total contributed hours` = for each contribution in `day's contributions[]` then `total contributed hours` += contribution.contributedHours

5. Finally subtract the `total contributed hours` from the `previous day's balance`

`day's balance` = `previous day's balance` - `total contributed hours`

### Example Scenarios

The scenarios below cover way the state of an arbitrary Accrual module changes in response to `TimeEntry` events that the Accruals container receives from the TimeCard container. The scenarios centre around the 24 June, 25 June and 26 June

For the purposes of the following worked examples assume that the balance on 24 June is 100. Exactly how this balance has been reached will not be broken down as it is not necessary to follow the examples through. The examples merely need a starting balance to be worked through.

For brevity some properties have been excluded from the examples below

#### Scenario 1:  Create a `TimeEntry` spanning a single day

begin state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 100 
    "contributions": []
  }  
]
```

end state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 90 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 08:00:00",
          "actualEndTime": "2022-06-25 18:00:00",
          "id":1
        },
        "contributedHours": 10
      }
    ]
  }  
]
```

#### Scenario 2: Delete an existing `TimeEntry` spanning a single day

begin state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 90 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 08:00:00",
          "actualEndTime": "2022-06-25 18:00:00",
          "id":1
        },
        "contributedHours": 10
      }
    ]
  }  
]
```

end state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 100 
    "contributions": []
  }  
]
```

#### Scenario 3: Move an existing `TimeEntry` spanning a single day to a different time on the same day


begin state: 

```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 90 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 08:00:00",
          "actualEndTime": "2022-06-25 18:00:00",
          "id":1
        },
        "contributedHours": 10
      }
    ]
  }  
]
```
end state: 

```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 96
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 09:00:00",
          "actualEndTime": "2022-06-25 13:00:00",
          "version": 2,
          "deleted": "false",
          "id":1
        },
        "contributedHours": 4 
      }      
    ]
  }
]
```

#### Scenario 4: Create a `TimeEntry` spanning two contiguous days

begin state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 100 
    "contributions": []
  },
  { 
    "date": "2022-06-26",
    "balance": 89,
    "contributions": []
  }  
]
```

end state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 95 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 19:00:00",
          "actualEndTime": "2022-06-26 06:00:00",
          "id":1
        },
        "contributedHours": 5
      }
    ]
  },
  { 
    "date": "2022-06-26",
    "balance": 89,
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 19:00:00",
          "actualEndTime": "2022-06-26 06:00:00",
          "id":1
        },
        "contributedHours": 6
      }
	]
  }  
]
```

#### Scenario 5: Move an existing `TimeEntry` spanning a single day to two contiguous days

begin state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 90 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 08:00:00",
          "actualEndTime": "2022-06-25 18:00:00",
          "id":1
        },
        "contributedHours": 10
      }
  },
  { 
    "date": "2022-06-26",
    "balance": 90,
    "contributions": []
  }  
]
```

end state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 95 
    "contributions": [
    "timeentry": 
       {
          "actualStartTime": "2022-06-25 19:00:00",
          "actualEndTime": "2022-06-26 06:00:00",
          "id":1
        },
        "contributedHours": 5
      }
    ]
  },
  { 
    "date": "2022-06-26",
    "balance": 89,
    "contributions": [
     {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 19:00:00",
          "actualEndTime": "2022-06-26 06:00:00",
          "id":1
        },
        "contributedHours": 6
      }
	]
  }  
]
```

#### Scenario 6: Move an existing `TimeEntry` spanning two contiguous days a single day

begin state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 95 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 19:00:00",
          "actualEndTime": "2022-06-26 06:00:00",
          "id":1
        },
        "contributedHours": 5
      }
    ]
  },
  { 
    "date": "2022-06-26",
    "balance": 89,
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 19:00:00",
          "actualEndTime": "2022-06-26 06:00:00",
          "id":1
        },
        "contributedHours": 6
      }
	]
  }  
]
```

end state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 90 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 08:00:00",
          "actualEndTime": "2022-06-25 18:00:00",
          "id":1
        },
        "contributedHours": 10
      },
    ]
  },
  { 
    "date": "2022-06-26",
    "balance": 90,
    "contributions": []
  }  
]
```

#### Scenario 7: Delete an existing `TimeEntry` spanning two contiguous days

begin state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 95 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 19:00:00",
          "actualEndTime": "2022-06-26 06:00:00",
          "id":1
        },
        "contributedHours": 5
      }
    ]
  },
  { 
    "date": "2022-06-26",
    "balance": 89,
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 19:00:00",
          "actualEndTime": "2022-06-26 06:00:00",
          "id":1
        },
        "contributedHours": 6
      }
	]
  }  
]
```

end state:
```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 100 
    "contributions": []
  },
  { 
    "date": "2022-06-26",
    "balance": 100,
    "contributions": []
  }  
]
```

#### Scenario 8: Move an existing `TimeEntry` spanning a single day to a different single day

begin state: 

```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 90 
    "contributions": [
      {
        "timeentry": 
        {
          "actualStartTime": "2022-06-25 08:00:00",
          "actualEndTime": "2022-06-25 18:00:00",
          "id":1
        },
        "contributedHours": 10
      }
    ]
  },
  { 
    "date": "2022-06-26",
    "balance": 90,
    "contributions": []
  }  
]
```
end state: 

```yaml
[
  { 
    "date": "2022-06-25",
    "balance": 100
    "contributions": []
  },
  { 
    "date": "2022-06-26",
    "balance": 90,
    "contributions": [
		{
        "timeentry": 
        {
          "actualStartTime": "2022-06-26 08:00:00",
          "actualEndTime": "2022-06-26 18:00:00",
          "id":1
        },
        "contributedHours": 10
      }          
    ]
  }  
]
```

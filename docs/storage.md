<h1 id="accruals">Accruals v0.1.0</h1>

The Accruals container stores a number of entities. Principle amongst them is the `accrual` table

![storage-model.png](./images/storage-model.png)

# Tables

<h2 id="tocS_Accrual">accrual</h2>

Represents the balance of an accrual on a given date.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|PK|true|none|the identifier for this Accrual|
|type_id|FK ([accrual_type](#tocS_Accrual_Type))|true|none|the type of this Accrual. Sometimes referred to as the "module"|
|balance|number|true|none|The remaining balance on for the accrual on the given date. Default is zero|
|date|date|true|none|The date that the Accrual is associated with|

<h2 id="tocS_Contribution">contribution</h2>

A contribution towards the balance of an Accrual

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|PK|true|none|the identifier for this Contribution|
|time_entry_id|FK ([time_entry](#tocS_TimeEntry))|true|none|The TimeEntry records an amount of time that backs the contributedHours property|
|accrual_id|FK ([accrual](#tocS_Accrual))|true|none|The balance of the `Accrual` that this `Contribution` effects|
|value|number|true|none|Holds the count that this `Contribution` negates the `Accrual`'s balance by|

**REF DATA - accrual type and units etc, name**
<h2 id="tocS_Accrual_Type">accrual_type</h2>

Reference data that linked to a type of `Accrual`

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|PK|true|none|the identifier for this `accrual_type` record|
|name|string|true|none|The human readable name for this `accrual_type` |
|measurement_unit|string|true|one of 'hours' or 'count'|The way that the balance of an Accrual of this `accrual_type` should be interpreted|

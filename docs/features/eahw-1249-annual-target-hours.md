# Annual Target Hours

This design covers how one goes about using the data from a `TimeEntry` event to identify the Accrual instances that require updating.

Two Accruals containers are involved in supporting this feature

1. Accrual balance calculator - performs the balance calculations and updates resources that hold balances
2. Accrual REST API - used by the calculator to retrieve and save data 

Follow the [balance calculation](./../containers/balance-calculator/orchestration/accrual-balance-calculation.md) design to understand how to work out what the balance of the Accrual instances should be.

The table below shows the individual tickets in the Annual Target Hours feature and also shows the how complete the design is for each ticket.

| Ticket                                                                                                                                           | Design           |
|--------------------------------------------------------------------------------------------------------------------------------------------------|------------------|
| [Annual Target Hours - Details Table ](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1624)                                        | [AccrualSummary](./../rest-operations.md#accrualsummary) |
| [Annual Target Hours - Visual Indicator Over/under/on Target (March)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-2048)                                       | [Target Status](#target-status) |
| [Annual Target Hours  - Visual Indicator Over/under/on Target (April to February)](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1899)                                         | [Target Status](#target-status) |
| [Annual Target hours - Negative Balance Indicator](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1738)                                                     | [AccrualSummary.agreementVariance](./../rest-operations.md#accrualsummary) |	
| [Annual Target Hours - Tolerance Rate Calculation ](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1490)                                                     | [Tolerance Rate Calculation](#tolerance-rate-calculation) |
| [Annual Target Hours - Net indication ](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1906)                                                     | [AccrualSummary.total & AccrualSummary.totalNetOrGrossOfPH](./../rest-operations.md#accrualsummary) |

## Tolerance rate calculation
The [AccrualSummary.targetVariance](./../rest-operations.md#accrualsummary) property will hold the surplus or deficit value against the target for the Annual target hours Accrual type. See the Jira ticket for details of the calculation.

## Target Status
The [AccrualSummary.targetStatus](./../rest-operations.md#accrualsummary) property will hold the indicator as to whether or not the worker is under, over or on target for Annual target hours. 

The Jira ticket refers to *"remaining balance"* this is calculated as follows - 

`remaining balance` = [`AccrualSummary.total`](./../rest-operations.md#accrualsummary) - [`AccrualSummary.remainingHighPrecision`](./../rest-operations.md#accrualsummary)

## Matching criteria
The data held in the `TimeEntry` event is used to determine whether or not the Annual target hours Accrual type should be updated.

More detail can be found in [Annual Target Hours - Count](https://collaboration.homeoffice.gov.uk/jira/browse/EAHW-1497). In summary the following events should be excluded from updating the Annual Target hours module -

1. `FlexChange`
2. `TimeEntry` where `TimeEntry.timePeriodType` signifies that the entry is for an on-call event

Other than that every time entry change that is attributed to a worker contributes to their Annual Target hours accrual module. 




-- =============================================================================
-- Description: Returns list of Accruals impacted by TimeEntry. Covering dates
--				from the earliest found impacted accrual - 1 day ('day before' used
-- 				in calculation of totals') till the end of agreement.
--
-- Parameters:
-- @startDate - TimeEntry start date (it can be before the first found accrual)
-- @timeEntryId
-- @agreementEndDate
--
-- Returns: Accruals table
-- =============================================================================
CREATE OR replace FUNCTION getImpactedAccrualsWithDayBefore(
startDate DATE, timeEntryId VARCHAR , agreementEndDate DATE)
returns table (
id VARCHAR (36),
tenant_id VARCHAR (36),
agreement_id VARCHAR (36),
accrual_date DATE,
accrual_type_id VARCHAR (36),
cumulative_total DECIMAL,
cumulative_target DECIMAL,
contributions JSONB,
person_id VARCHAR (36)
)
language plpgsql
as'
begin
return query
WITH
-- get date of 1st contribution
t1 AS (SELECT *, startDate AS timeentrystartdate FROM accruals.accrual AS ac
WHERE (ac.contributions -> ''timeEntries'') ->> timeEntryId != ''null''
ORDER BY ac.accrual_date LIMIT 1)
SELECT ac.*
FROM accruals.accrual ac
WHERE ac.person_id = (SELECT t1.person_id FROM t1)
AND ac.tenant_id = (SELECT t1.tenant_id FROM t1)
AND ac.accrual_date
BETWEEN
(SELECT least(t1.accrual_date, t1.timeentrystartdate) - 1 FROM t1)
AND agreementEndDate
ORDER BY ac.accrual_date;
end;';
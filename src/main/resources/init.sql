CREATE SCHEMA IF NOT EXISTS accruals;

create or replace function getImpactedAccrualsWithDayBefore(
			startDate date, timeEntryId VARCHAR, agreementEndDate date)
returns table (
	id varchar,
    tenant_id varchar,
    agreement_id varchar,
    accrual_date DATE,
    accrual_type_id varchar,
    cumulative_total DECIMAL,
    cumulative_target DECIMAL,
    contributions JSONB,
	person_id varchar
)
language plpgsql
as'
begin
return query
    WITH t1 AS (SELECT *, startDate AS timeentrystartdate FROM accruals.accrual AS ac
				WHERE (ac.contributions -> ''timeEntries'') ->> timeEntryId != ''null''
				ORDER BY ac.accrual_date LIMIT 1)
    SELECT
        CAST(ac.id as CHARACTER VARYING(36)) as id,
        CAST(ac.tenant_id as CHARACTER VARYING(36)) as tenant_id,
        CAST(ac.agreement_id as CHARACTER VARYING(36)) as agreement_id,
        ac.accrual_date,
        CAST(ac.accrual_type_id as CHARACTER VARYING(36)) as accrual_type_id,
        ac.cumulative_total,
        ac.cumulative_target,
        ac.contributions,
        CAST(ac.person_id as CHARACTER VARYING(36)) as person_id
    FROM accruals.accrual ac
    WHERE ac.person_id = (SELECT t1.person_id FROM t1)
      AND ac.tenant_id = (SELECT t1.tenant_id FROM t1)
      AND ac.accrual_date
        BETWEEN
        (SELECT least(t1.accrual_date, t1.timeentrystartdate) - 1 FROM t1)
        AND agreementEndDate
    ORDER BY ac.accrual_date;
end;';

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

CREATE OR REPLACE FUNCTION get_impacted_accruals(
        time_entry_start_date date, time_entry_id varchar, agreement_end_date date)
RETURNS SETOF accruals.accrual
LANGUAGE plpgsql
AS'
DECLARE
earliest_contribution_date DATE;
min_date DATE;
personid VARCHAR;
tenantid VARCHAR;
BEGIN
		-- get date of the earliest TimeEntry contribution
        SELECT ac.accrual_date, ac.person_id, ac.tenant_id
        INTO earliest_contribution_date, personid, tenantid
        FROM accruals.accrual AS ac
        WHERE (ac.contributions -> ''timeEntries'') ->> time_entry_id != ''null''
        ORDER BY ac.accrual_date LIMIT 1;

        -- get min date
        SELECT least(earliest_contribution_date, time_entry_start_date) INTO min_date;

        RETURN QUERY
        SELECT ac.*
        FROM accruals.accrual ac
        WHERE ac.person_id = personid
          AND ac.tenant_id = tenantid
          AND ac.accrual_date
            BETWEEN min_date - 1 AND agreement_end_date
        ORDER BY ac.accrual_date;
END;';

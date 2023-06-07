-- =============================================================================
-- Description: Returns list of Accruals impacted by TimeEntry. Covering dates
--              from the earliest found impacted accrual - 1 day ('day before' used
--              in calculation of totals') till the end of agreement.
--
-- Parameters:
-- @tenant_id
-- @time_entry_start_date - TimeEntry start date (it can be before the first found accrual)
-- @time_entry_id
-- @agreement_end_date
--
-- Returns: Accruals table
-- =============================================================================

CREATE OR REPLACE FUNCTION accruals.get_impacted_accruals(
    p_tenant_id VARCHAR, time_entry_start_date DATE, time_entry_id VARCHAR, agreement_end_date DATE)
    RETURNS SETOF accruals.accrual
    LANGUAGE plpgsql
AS'
    DECLARE
        earliest_contribution_date DATE;
        min_date DATE;
        v_person_id VARCHAR;
    BEGIN
        -- get date of the earliest TimeEntry contribution
        SELECT ac.accrual_date, ac.person_id
        INTO earliest_contribution_date, v_person_id
        FROM accruals.accrual AS ac
        WHERE (ac.contributions -> ''timeEntries'') ->> time_entry_id != ''null''
        ORDER BY ac.accrual_date LIMIT 1;

        -- get min date
        SELECT least(earliest_contribution_date, time_entry_start_date) INTO min_date;

        RETURN QUERY
            SELECT ac.*
            FROM accruals.accrual ac
            WHERE ac.person_id = v_person_id
              AND ac.tenant_id = p_tenant_id
              AND ac.accrual_date
                BETWEEN min_date - 1 AND agreement_end_date
            ORDER BY ac.accrual_date;
    END;';

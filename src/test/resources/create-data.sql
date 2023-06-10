DO ' DECLARE
    tenant_id VARCHAR := ''00000000-0000-0000-0000-000000000000'';
    person_id VARCHAR := ''00000000-0000-0000-0000-000000000001'';
    agreement1_id VARCHAR := gen_random_uuid();
    agreement1_start_date CONSTANT DATE := ''2022-04-01'';
    agreement1_end_date CONSTANT DATE := ''2022-04-05'';
    agreement2_id VARCHAR := gen_random_uuid();
    agreement2_start_date CONSTANT DATE := ''2022-04-06'';
    agreement2_end_date CONSTANT DATE := ''2022-04-10'';
BEGIN

    INSERT INTO accruals.agreement (
        id,
        tenant_id,
        person_id,
        start_date,
        end_date
    )
    VALUES (
               agreement1_id,
               tenant_id,
               person_id,
               agreement1_start_date,
               agreement1_end_date
           ), (
               agreement2_id,
               tenant_id,
               person_id,
               agreement2_start_date,
               agreement2_end_date
           );

    INSERT INTO accruals.accrual (
        id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions
    )
    VALUES (
        gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-01'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 1, "timeEntries": {"10000000-0000-0000-0000-000000000001": 1}}''
    );

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-02'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 2, "timeEntries": {"10000000-0000-0000-0000-000000000002": 2}}'');

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-03'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 3, "timeEntries": {"10000000-0000-0000-0000-000000000003": 3}}'');

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-04'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 4, "timeEntries": {"10000000-0000-0000-0000-000000000004": 4}}'');

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-05'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 5, "timeEntries": {"10000000-0000-0000-0000-000000000005": 5}}'');

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-06'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 6, "timeEntries": {"10000000-0000-0000-0000-000000000006": 6}}'');

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-07'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 7, "timeEntries": {"10000000-0000-0000-0000-000000000007": 7}}'');

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-08'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 8, "timeEntries": {"10000000-0000-0000-0000-000000000008": 8}}'');

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-09'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 9, "timeEntries": {"10000000-0000-0000-0000-000000000009": 9}}'');

    INSERT INTO accruals.accrual (id,
        tenant_id,
        agreement_id,
        accrual_date,
        accrual_type_id,
        cumulative_total,
        cumulative_target,
        person_id,
        contributions)
    VALUES (gen_random_uuid(),
        tenant_id,
        agreement1_id,
        ''2022-04-10'',
        ''00000000-0000-0000-0000-000000000000'',
        0,
        0,
        person_id,
        ''{"total": 10, "timeEntries": {"10000000-0000-0000-0000-000000000005": 10}}'');

END;';
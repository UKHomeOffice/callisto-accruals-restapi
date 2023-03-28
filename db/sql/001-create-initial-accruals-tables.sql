CREATE TABLE IF NOT EXISTS accruals.agreement (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    person_id VARCHAR(36) NOT NULL,
    version INT NOT NULL DEFAULT 1,
    fte_value NUMERIC (5,4) NOT NULL,
    terms_and_conditions TEXT CHECK (terms_and_conditions IN ('MODERNISED', 'PRE_MODERNISED')),
    salary_basis TEXT CHECK (salary_basis IN ('LONDON', 'LONDON_PROVINCIAL', 'NATIONAL')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS accruals.accrual_type (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    name VARCHAR NOT NULL,
    measurement_unit TEXT CHECK (measurement_unit IN ('HOURS', 'COUNT'))
);

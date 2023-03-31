CREATE TABLE accruals.agreement (
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

CREATE TABLE accruals.agreement_target (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    agreement_id VARCHAR(36) NOT NULL,
    accrual_type_id VARCHAR(36) NOT NULL,
    target_total DECIMAL NOT NULL, -- TODO: CHECK DECIMALS
    CONSTRAINT fk_agreement FOREIGN KEY(agreement_id) REFERENCES accruals.agreement(id)
);

CREATE INDEX ON accruals.agreement_target (agreement_id);

CREATE TABLE accruals.accrual (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    agreement_id VARCHAR(36) NOT NULL,
    date DATE NOT NULL,
    accrual_type_id VARCHAR(36) NOT NULL,
    cumulative_total DECIMAL NOT NULL,
    cumulative_target DECIMAL NOT NULL,
    contributions JSONB NULL,
    CONSTRAINT fk_agreement FOREIGN KEY(agreement_id) REFERENCES accruals.agreement(id)
);

CREATE INDEX ON accruals.accrual (agreement_id);

-- TODO: Add unique constraints
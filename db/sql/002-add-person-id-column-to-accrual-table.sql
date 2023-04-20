ALTER TABLE accruals.accrual ADD COLUMN person_id VARCHAR(36) NOT NULL;

CREATE INDEX ON accruals.accrual (tenant_id, person_id, accrual_date);
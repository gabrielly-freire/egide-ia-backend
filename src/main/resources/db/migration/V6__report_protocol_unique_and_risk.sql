UPDATE report
SET protocol_number = 'PM' || id
WHERE protocol_number IS NULL;

ALTER TABLE report
    ADD CONSTRAINT uk_report_protocol_number UNIQUE (protocol_number);

ALTER TABLE report_processed
    ADD COLUMN risk VARCHAR(20);

ALTER TABLE report_processed
    ADD CONSTRAINT ck_report_processed_risk
        CHECK (risk IN ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW'));

ALTER TABLE report_ai_analysed
    ADD COLUMN risk VARCHAR(20);

ALTER TABLE report_ai_analysed
    ADD CONSTRAINT ck_report_ai_analysed_risk
        CHECK (risk IN ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW'));
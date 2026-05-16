CREATE TABLE satisfaction_survey (
    id BIGSERIAL PRIMARY KEY,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    report_id BIGINT NOT NULL,
    speed_rating INTEGER NOT NULL,
    resolution_rating INTEGER NOT NULL,
    comments TEXT,
    CONSTRAINT fk_survey_report FOREIGN KEY (report_id) REFERENCES report(id)
);
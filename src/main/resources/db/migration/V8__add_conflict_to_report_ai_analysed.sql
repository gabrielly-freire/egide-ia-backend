ALTER TABLE report_ai_analysed
    RENAME COLUMN has_conflict TO conflict_detected;

ALTER TABLE report_ai_analysed
    ADD COLUMN manager_conflict BOOLEAN;

CREATE TABLE report_ai_analysed_conflicted_user_ids (
    report_ai_analysed_id BIGINT NOT NULL,
    conflicted_user_id    VARCHAR(255),
    CONSTRAINT fk_raacui_analysed
        FOREIGN KEY (report_ai_analysed_id)
        REFERENCES report_ai_analysed (id) ON DELETE CASCADE
);

ALTER TABLE report ALTER COLUMN description TYPE TEXT;
ALTER TABLE report ALTER COLUMN title TYPE TEXT;

ALTER TABLE report_ai_analysed ALTER COLUMN description_anonymized TYPE TEXT;
ALTER TABLE report_ai_analysed ALTER COLUMN title_anonymized TYPE TEXT;

ALTER TABLE report_processed ALTER COLUMN description_anonymized TYPE TEXT;
ALTER TABLE report_processed ALTER COLUMN title_anonymized TYPE TEXT;
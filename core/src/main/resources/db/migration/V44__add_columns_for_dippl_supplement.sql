ALTER TABLE speciality ADD COLUMN entrance_certificates VARCHAR(150) DEFAULT '' NOT NULL;
ALTER TABLE speciality ADD COLUMN entrance_certificates_eng VARCHAR(150) DEFAULT '' NOT NULL;

ALTER TABLE specialization ADD COLUMN normative_credits_number INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE specialization ADD COLUMN normative_term_of_study numeric(10,2) DEFAULT 0 NOT NULL;

UPDATE specialization SET (normative_credits_number, normative_term_of_study) VALUES (240, 3.84) WHERE active=true and degree_id=1
UPDATE specialization SET (normative_credits_number, normative_term_of_study) VALUES (90, 1.42) WHERE active=true and degree_id=3


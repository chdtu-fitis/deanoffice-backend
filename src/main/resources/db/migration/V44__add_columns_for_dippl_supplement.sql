ALTER TABLE degree DROP COLUMN admission_shortened_requirements;
ALTER TABLE degree DROP COLUMN admission_shortened_requirements_eng;

UPDATE degree SET admission_requirements='' WHERE admission_requirements IS NULL;
UPDATE degree SET admission_requirements_eng='' WHERE admission_requirements_eng IS NULL;
UPDATE degree SET admission_foreign_requirements='' WHERE admission_foreign_requirements IS NULL;
UPDATE degree SET admission_foreign_requirements_eng='' WHERE admission_foreign_requirements_eng IS NULL;
UPDATE degree SET further_study_access='' WHERE further_study_access IS NULL;
UPDATE degree SET further_study_access_eng='' WHERE further_study_access_eng IS NULL;
UPDATE degree SET qualification_level_descr='' WHERE qualification_level_descr IS NULL;
UPDATE degree SET qualification_level_descr_eng='' WHERE qualification_level_descr_eng IS NULL;
ALTER TABLE degree ALTER COLUMN name_eng SET NOT NULL;
ALTER TABLE degree ALTER COLUMN admission_requirements SET NOT NULL;
ALTER TABLE degree ALTER COLUMN admission_requirements_eng SET NOT NULL;
ALTER TABLE degree ALTER COLUMN admission_foreign_requirements SET NOT NULL;
ALTER TABLE degree ALTER COLUMN admission_foreign_requirements_eng SET NOT NULL;
ALTER TABLE degree ALTER COLUMN further_study_access SET NOT NULL;
ALTER TABLE degree ALTER COLUMN further_study_access_eng SET NOT NULL;
ALTER TABLE degree ALTER COLUMN qualification_level_descr SET NOT NULL;
ALTER TABLE degree ALTER COLUMN qualification_level_descr_eng SET NOT NULL;

ALTER TABLE degree ALTER COLUMN admission_requirements TYPE VARCHAR(300);
ALTER TABLE degree ALTER COLUMN admission_requirements_eng TYPE VARCHAR(300);
ALTER TABLE degree ALTER COLUMN admission_foreign_requirements TYPE VARCHAR(300);
ALTER TABLE degree ALTER COLUMN admission_foreign_requirements_eng TYPE VARCHAR(300);

ALTER TABLE speciality ADD COLUMN entrance_certificates VARCHAR(150) DEFAULT '' NOT NULL;
ALTER TABLE speciality ADD COLUMN entrance_certificates_eng VARCHAR(150) DEFAULT '' NOT NULL;

ALTER TABLE specialization ADD COLUMN normative_credits_number INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE specialization ADD COLUMN normative_term_of_study numeric(10,2) DEFAULT 0 NOT NULL;

UPDATE specialization SET normative_credits_number=240, normative_term_of_study=3.84 WHERE active=true and degree_id=1;
UPDATE specialization SET normative_credits_number=90, normative_term_of_study=1.42 WHERE active=true and degree_id=3;


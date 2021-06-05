ALTER TABLE selective_courses_year_parameters ADD COLUMN period_case VARCHAR(5) NOT NULL;
ALTER TABLE selective_courses_year_parameters DROP CONSTRAINT uk_sc_year_parameters_study_year;

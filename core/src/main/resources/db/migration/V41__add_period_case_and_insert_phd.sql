ALTER TABLE selective_courses_year_parameters ADD COLUMN period_case VARCHAR(5) NOT NULL;
INSERT INTO degree(id, name, name_eng) VALUES (4, 'Доктор філософії', 'Philosophy Doctor');
ALTER TABLE selective_courses_year_parameters DROP CONSTRAINT uk_sc_year_parameters_study_year;
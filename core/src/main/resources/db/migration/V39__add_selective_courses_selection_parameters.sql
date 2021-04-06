CREATE TABLE selective_courses_year_parameters(
    id SERIAL PRIMARY KEY,
    first_round_start_date DATE NOT NULL,
    first_round_end_date DATE NOT NULL,
    second_round_end_date DATE NOT NULL,
    min_students_count INTEGER NOT NULL
);

ALTER TABLE selective_courses_year_parameters
    ADD CONSTRAINT uk_year_parameters_fr_start_date UNIQUE(first_round_start_date);

ALTER TABLE selective_courses_year_parameters
    ADD CONSTRAINT uk_year_parameters_fr_end_date UNIQUE(first_round_end_date);

ALTER TABLE selective_courses_year_parameters
    ADD CONSTRAINT uk_year_parameters_sr_end_date UNIQUE(second_round_end_date);

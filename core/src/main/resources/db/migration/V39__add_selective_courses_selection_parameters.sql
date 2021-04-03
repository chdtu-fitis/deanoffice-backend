CREATE TABLE selective_courses_selection_parameters(
    id SERIAL PRIMARY KEY,
    first_round_start_date DATE NOT NULL,
    first_round_end_date DATE NOT NULL,
    second_round_start_date DATE NOT NULL,
    second_round_end_date DATE NOT NULL,
    minimum_count_of_students INTEGER NOT NULL
);

ALTER TABLE selective_courses_selection_parameters
    ADD CONSTRAINT uk_selective_courses_selection_parameters_first_round_start_date UNIQUE(first_round_start_date);

ALTER TABLE selective_courses_selection_parameters
    ADD CONSTRAINT uk_selective_courses_selection_parameters_first_round_end_date UNIQUE(first_round_end_date);

ALTER TABLE selective_courses_selection_parameters
    ADD CONSTRAINT uk_selective_courses_selection_parameters_second_round_start_date UNIQUE(second_round_end_date);

ALTER TABLE selective_courses_selection_parameters
    ADD CONSTRAINT uk_selective_courses_selection_parameters_second_round_end_date UNIQUE(second_round_start_date);

CREATE TABLE student_previous_university (
    id SERIAL PRIMARY KEY,
    student_degree_id integer NOT NULL,
    university_name character varying(150) NOT NULL,
    study_start_date date NOT NULL,
    study_end_date date NOT NULL,
    academic_certificate_number character varying(15),
    academic_certificate_date date
);

ALTER TABLE student_previous_university 
    ADD CONSTRAINT ukvtr97cwy234e4ll4g11u2dtb1z UNIQUE (student_degree_id, study_start_date);

ALTER TABLE student_previous_university
    ADD CONSTRAINT fkq768ubwr4vwmf2y6rh0kdf1n9 FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);

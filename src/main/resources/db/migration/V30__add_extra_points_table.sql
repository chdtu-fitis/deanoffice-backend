CREATE TABLE extra_points (
    id                            SERIAL PRIMARY KEY,
    student_degree_id             integer      NOT NULL,
    semester                      integer      NOT NULL,
    points                        integer      NOT NULL
);

ALTER TABLE extra_points
    ADD CONSTRAINT fk_extra_points_student_degree_id FOREIGN KEY (student_degree_id) REFERENCES student_degree;

ALTER TABLE extra_points
    ADD CONSTRAINT uk_extra_points_degree_semester UNIQUE (student_degree_id, semester);

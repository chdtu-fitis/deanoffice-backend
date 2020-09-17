CREATE TABLE one_course_groups_student_degrees(
    id SERIAL PRIMARY KEY,
    student_group_id INTEGER NOT NULL,
    student_degree_id INTEGER NOT NULL
);

ALTER TABLE one_course_groups_student_degrees
    ADD CONSTRAINT fk_one_course_groups_student_degrees_student_group_id FOREIGN KEY (student_group_id) REFERENCES student_group(id);
ALTER TABLE one_course_groups_student_degrees
    ADD CONSTRAINT fk_one_course_groups_student_degrees_student_degree_id FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);
ALTER TABLE one_course_groups_student_degrees
    ADD CONSTRAINT uk_one_course_groups_student_degrees_student_degree_id_student_group_id UNIQUE(student_degree_id, student_group_id);

CREATE TABLE selective_course (
    id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL,
    teacher_id INTEGER,
    study_year INTEGER NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE selective_course
    ADD CONSTRAINT fk_selective_course_teacher_id FOREIGN KEY (teacher_id) REFERENCES teacher(id);
ALTER TABLE selective_course
    ADD CONSTRAINT fk_selective_course_course_id FOREIGN KEY (course_id) REFERENCES course(id);
ALTER TABLE selective_course
    ADD CONSTRAINT uk_selective_course_course_id_study_year UNIQUE(course_id, study_year);


CREATE TABLE selective_courses_student_degrees(
    id SERIAL PRIMARY KEY,
    selective_course_id INTEGER NOT NULL,
    student_degree_id INTEGER NOT NULL
);

ALTER TABLE selective_courses_student_degrees
    ADD CONSTRAINT fk_selective_courses_student_degrees_selective_course_id FOREIGN KEY (selective_course_id) REFERENCES selective_course(id);
ALTER TABLE selective_courses_student_degrees
    ADD CONSTRAINT fk_selective_courses_student_degrees_student_degree_id FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);
ALTER TABLE selective_courses_student_degrees
    ADD CONSTRAINT uk_selective_course_student_degrees_course_id_student_degree_id UNIQUE(selective_course_id, student_degree_id);

ALTER TABLE student_group
    ADD COLUMN one_course BOOLEAN DEFAULT FALSE;
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


CREATE TABLE field_of_knowledge(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50),
    name_eng VARCHAR(50)
);

CREATE TABLE selective_course (
    id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL,
    teacher_id INTEGER,
    study_year INTEGER NOT NULL,
    degree_id INTEGER NOT NULL,
    training_cycle VARCHAR(50) NOT NULL,
    department_id INTEGER NOT NULL,
    basic_field_of_knowledge_id INTEGER NOT NULL,
    other_field_of_knowledge VARCHAR(70),
    description VARCHAR(200) NOt NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE selective_course
    ADD CONSTRAINT fk_selective_course_teacher_id FOREIGN KEY (teacher_id) REFERENCES teacher(id);
ALTER TABLE selective_course
    ADD CONSTRAINT fk_selective_course_course_id FOREIGN KEY (course_id) REFERENCES course(id);
ALTER TABLE selective_course
    ADD CONSTRAINT fk_selective_course_degree_id FOREIGN KEY (degree_id) REFERENCES degree(id);
ALTER TABLE selective_course
    ADD CONSTRAINT fk_selective_course_department_id FOREIGN KEY (department_id) REFERENCES department(id);
ALTER TABLE selective_course
    ADD CONSTRAINT fk_selective_course_basic_field_of_knowledge_id FOREIGN KEY (basic_field_of_knowledge_id) REFERENCES field_of_knowledge(id);
ALTER TABLE selective_course
    ADD CONSTRAINT uk_selective_course_course_id_study_year_degree_id_department_id UNIQUE(course_id, study_year, degree_id, department_id);

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
CREATE TABLE courses_for_students(
   id SERIAL PRIMARY KEY,
   student_degree_id INTEGER NOT NULL,
   course_id INTEGER NOT NULL,
   teacher_id INTEGER,
   course_type VARCHAR(20) NOT NULL
);

ALTER TABLE courses_for_students
    ADD CONSTRAINT uk_courses_for_students UNIQUE (student_degree_id, course_id);
ALTER TABLE courses_for_students
    ADD CONSTRAINT fk_courses_for_students_student_degree_id FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);
ALTER TABLE courses_for_students
    ADD CONSTRAINT fk_courses_for_students_course_id FOREIGN KEY (course_id) REFERENCES course(id);
ALTER TABLE courses_for_students
    ADD CONSTRAINT fk_courses_for_students_teacher_id FOREIGN KEY (teacher_id) REFERENCES teacher(id);

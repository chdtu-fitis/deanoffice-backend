CREATE TABLE field_of_knowledge(
    id SERIAL PRIMARY KEY,
    code VARCHAR(15) NOT NULL,
    name VARCHAR(100) NOT NULL,
    name_eng VARCHAR(100) NOT NULL
);

ALTER TABLE field_of_knowledge
    ADD CONSTRAINT uk_field_of_knowledge_name_code UNIQUE (code, name);

CREATE TABLE selective_course (
    id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL,
    teacher_id INTEGER,
    study_year INTEGER NOT NULL,
    degree_id INTEGER NOT NULL,
    training_cycle VARCHAR(15) NOT NULL,
    department_id INTEGER NOT NULL,
    field_of_knowledge_id INTEGER,
    other_fields_of_knowledge VARCHAR(25),
    description VARCHAR(1024) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    group_name VARCHAR(20)
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
    ADD CONSTRAINT fk_selective_course_field_of_knowledge_id FOREIGN KEY (field_of_knowledge_id) REFERENCES field_of_knowledge(id);
ALTER TABLE selective_course
    ADD CONSTRAINT uk_selective_course_course_id_study_year_degree_id UNIQUE(course_id, study_year, degree_id);

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

INSERT INTO field_of_knowledge (code, name, name_eng) VALUES
	('02', 'Культура і мистецтво', 'Culture and Arts'),
	('03', 'Гуманітарні науки', 'Humanities'),
	('05', 'Соціальні та поведінкові науки', 'Social and Behavioral Studies'),
	('07', 'Управління та адміністрування', 'Management and Administration'),
	('08', 'Право', 'Law'),
	('10', 'Природничі науки', 'Natural Sciences'),
	('12', 'Інформаційні технології', 'Information Technologies'),
	('13', 'Механічна інженерія', 'Mechanical Engineering'),
	('14', 'Електрична інженерія', 'Electrical Engineering'),
	('15', 'Автоматизація та приладобудування', 'Automation and Instrumentation'),
	('16', 'Хімічна та біоінженерія', 'Chemical and Bioengineering'),
	('17', 'Електроніка та телекомунікації', 'Electronics and Telecommunications'),
	('18', 'Виробництво та технології', 'Manufacturing and Technology'),
	('19', 'Архітектура та будівництво', 'Architecture and Construction'),
	('23', 'Соціальна робота', 'Social Work'),
	('24', 'Сфера обслуговування', 'Services'),
	('27', 'Транспорт', 'Transport Services'),
	('28', 'Публічне управління та адміністрування', 'Public Administration');

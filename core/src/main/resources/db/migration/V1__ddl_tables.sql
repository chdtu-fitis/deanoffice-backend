CREATE TABLE course (
    id SERIAL PRIMARY KEY,
    credits numeric(4,1) NOT NULL,
    hours integer NOT NULL,
    hours_per_credit integer NOT NULL,
    semester integer NOT NULL,
    course_name_id integer,
    kc_id integer
);

CREATE TABLE course_name (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    name_eng character varying(100),
    abbreviation character varying(15)
);

CREATE TABLE courses_for_groups (
    id SERIAL PRIMARY KEY,
    exam_date date,
    course_id integer NOT NULL,
    student_group_id integer NOT NULL,
    teacher_id integer
);

CREATE TABLE current_year (
    id SERIAL PRIMARY KEY,
    curr_year integer
);

CREATE TABLE degree (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    name_eng character varying(100),
    admission_requirements character varying(255),
    admission_requirements_eng character varying(255),
    further_study_access character varying(255),
    further_study_access_eng character varying(255),
    professional_status character varying(255),
    professional_status_eng character varying(255),
    qualification_level_descr character varying(255),
    qualification_level_descr_eng character varying(255)
);

CREATE TABLE department (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    active boolean NOT NULL,
    abbr character varying(20) NOT NULL,
    faculty_id integer NOT NULL
);

CREATE TABLE faculty (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    name_eng character varying(100),
    active boolean NOT NULL,
    abbr character varying(20) NOT NULL,
    dean character varying(70)
);

CREATE TABLE grade (
    id SERIAL PRIMARY KEY,
    ects character varying(2),
    grade integer,
    points integer,
    course_id integer NOT NULL,
    student_degree_id integer NOT NULL
);

CREATE TABLE knowledge_control (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    name_eng character varying(100),
    graded boolean NOT NULL
);

CREATE TABLE order_reason (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    active boolean NOT NULL,
    kind character varying(25) NOT NULL
);

CREATE TABLE position (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL
);

CREATE TABLE privilege (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    active boolean NOT NULL
);

CREATE TABLE renewed_academic_vacation_student (
    id SERIAL PRIMARY KEY,
    application_date date NOT NULL,
    payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    renew_date date,
    study_year integer NOT NULL,
    student_academic_vacation_id integer NOT NULL,
    student_group_id integer NOT NULL
);

CREATE TABLE renewed_expelled_student (
    id SERIAL PRIMARY KEY,
    academic_certificate_date date NOT NULL,
    academic_certificate_issued_by character varying(255) NOT NULL,
    academic_certificate_number character varying(255) NOT NULL,
    application_date date NOT NULL,
    payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    renew_date date,
    study_year integer NOT NULL,
    student_expel_id integer NOT NULL,
    student_group_id integer NOT NULL
);

CREATE TABLE speciality (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    name_eng character varying(100),
    active boolean NOT NULL,
    code character varying(20) NOT NULL,
    field_of_study character varying(150),
    field_of_study_eng character varying(150)
);

CREATE TABLE specialization (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    name_eng character varying(100),
    active boolean NOT NULL,
    applying_knowledge_and_understanding_outcomes character varying(1200),
    applying_knowledge_and_understanding_outcomes_eng character varying(1200),
    program_head_info character varying(255) NOT NULL,
    program_head_info_eng character varying(255) NOT NULL,
    program_head_name character varying(255) NOT NULL,
    program_head_name_eng character varying(255) NOT NULL,
    knowledge_and_understanding_outcomes character varying(1200),
    knowledge_and_understanding_outcomes_eng character varying(1200),
    making_judgements_outcomes character varying(1200),
    making_judgements_outcomes_eng character varying(1200),
    payment_extramural numeric(15,2),
    payment_fulltime numeric(15,2),
    qualification character varying(100),
    qualification_eng character varying(100),
    degree_id integer NOT NULL,
    department_id integer,
    faculty_id integer NOT NULL,
    speciality_id integer NOT NULL
);

CREATE TABLE student (
    id SERIAL PRIMARY KEY,
    name character varying(20) NOT NULL,
    patronimic character varying(20),
    sex character varying(6) DEFAULT 'MALE'::character varying NOT NULL,
    surname character varying(20) NOT NULL,
    actual_address character varying(100),
    birth_date date,
    email character varying(30),
    father_info character varying(70),
    father_name character varying(40),
    father_phone character varying(20),
    mother_info character varying(70),
    mother_name character varying(40),
    mother_phone character varying(20),
    name_eng character varying(20),
    notes character varying(150),
    patronimic_eng character varying(20),
    photo bytea,
    registration_address character varying(100),
    school character varying(100),
    surname_eng character varying(20),
    telephone character varying(30),
    privilege_id integer
);

CREATE TABLE student_academic_vacation (
    id SERIAL PRIMARY KEY,
    application_date date NOT NULL,
    extra_information character varying(255),
    order_date date NOT NULL,
    order_number character varying(15) NOT NULL,
    study_year integer NOT NULL,
    vacation_end_date date NOT NULL,
    vacation_start_date date NOT NULL,
    order_reason_id integer NOT NULL,
    student_degree_id integer NOT NULL,
    student_group_id integer NOT NULL
);

CREATE TABLE student_degree (
    id SERIAL PRIMARY KEY,
    active boolean NOT NULL,
    admission_order_date date,
    admission_order_number character varying(15),
    contract_date date,
    contract_number character varying(15),
    diploma_date date,
    diploma_number character varying(15),
    payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    previous_diploma_date date,
    previous_diploma_number character varying(15),
    previous_diploma_type character varying(30) DEFAULT 'SECONDARY_SCHOOL_CERTIFICATE'::character varying NOT NULL,
    protocol_date date,
    protocol_number character varying(10),
    record_book_number character varying(15),
    student_card_number character varying(15),
    supplement_date date,
    supplement_number character varying(15),
    thesis_name character varying(150),
    thesis_name_eng character varying(150),
    degree_id integer NOT NULL,
    specialization_id integer NOT NULL,
    student_id integer NOT NULL,
    student_group_id integer
);

CREATE TABLE student_expel (
    id SERIAL PRIMARY KEY,
    application_date date NOT NULL,
    expel_date date NOT NULL,
    order_date date NOT NULL,
    order_number character varying(15) NOT NULL,
    payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    study_year integer NOT NULL,
    order_reason_id integer NOT NULL,
    student_degree_id integer NOT NULL,
    student_group_id integer NOT NULL
);

CREATE TABLE student_group (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    active boolean NOT NULL,
    begin_years integer NOT NULL,
    creation_year integer NOT NULL,
    study_semesters integer NOT NULL,
    study_years numeric(19,2) NOT NULL,
    tuition_form character varying(10) DEFAULT 'FULL_TIME'::character varying NOT NULL,
    tuition_term character varying(10) DEFAULT 'REGULAR'::character varying NOT NULL,
    specialization_id integer NOT NULL
);

CREATE TABLE teacher (
    id SERIAL PRIMARY KEY,
    name character varying(20) NOT NULL,
    patronimic character varying(20),
    sex character varying(6) DEFAULT 'MALE'::character varying NOT NULL,
    surname character varying(20) NOT NULL,
    active boolean NOT NULL,
    scientific_degree character varying(255),
    department_id integer NOT NULL,
    position_id integer NOT NULL
);

ALTER TABLE courses_for_groups
    ADD CONSTRAINT uk19sieop4l7esqbqmc6ictdvph UNIQUE (course_id, student_group_id);

ALTER TABLE order_reason
    ADD CONSTRAINT uk1otnvcettwbi4id43dh8d5sis UNIQUE (name);

ALTER TABLE faculty
    ADD CONSTRAINT uk7vcysepqmv2k09mdfa18gj8qw UNIQUE (name);

ALTER TABLE faculty
    ADD CONSTRAINT uk_757mlj4kyjn7mpb0fb67owj00 UNIQUE (abbr);

ALTER TABLE speciality
    ADD CONSTRAINT uk_f7cgfesjcj3ygekov90nlvnaa UNIQUE (code);

ALTER TABLE department
    ADD CONSTRAINT ukbiw7tevwc07g3iutlbmkes0cm UNIQUE (name);

ALTER TABLE degree
    ADD CONSTRAINT ukby27bbt64p1ria17hy3khpyft UNIQUE (name);

ALTER TABLE position
    ADD CONSTRAINT ukhct1althd622w7vj7630fvoh3 UNIQUE (name);

ALTER TABLE course_name
    ADD CONSTRAINT ukmcev2xfgvg6v572t3h91d7gap UNIQUE (name);

ALTER TABLE knowledge_control
    ADD CONSTRAINT uksvc0hwsgvfil1om8pvh41ay2c UNIQUE (name);

ALTER TABLE grade
    ADD CONSTRAINT ukt7cwyqn470e4ll4g11g1dib1p UNIQUE (course_id, student_degree_id);

ALTER TABLE privilege
    ADD CONSTRAINT uktcjtkyhbxekm2p1hsv6ju9bju UNIQUE (name);

ALTER TABLE student_academic_vacation
    ADD CONSTRAINT fk1aj9uc687ivslkx3tspud1n9y FOREIGN KEY (order_reason_id) REFERENCES order_reason(id);

ALTER TABLE courses_for_groups
    ADD CONSTRAINT fk1t8vjy9y1j7cf7s5dorx1t1it FOREIGN KEY (teacher_id) REFERENCES teacher(id);

ALTER TABLE course
    ADD CONSTRAINT fk2wuyv2xldcdi3x9srcaicq89g FOREIGN KEY (kc_id) REFERENCES knowledge_control(id);

ALTER TABLE teacher
    ADD CONSTRAINT fk3ebf13bw5jxpbch977xtuxrax FOREIGN KEY (position_id) REFERENCES position(id);

ALTER TABLE courses_for_groups
    ADD CONSTRAINT fk3icq8kpwq3vo1hko4sntb0hin FOREIGN KEY (course_id) REFERENCES course(id);

ALTER TABLE department
    ADD CONSTRAINT fk41ox44lg0tyunaxq4y8oyuq8m FOREIGN KEY (faculty_id) REFERENCES faculty(id);

ALTER TABLE student_expel
    ADD CONSTRAINT fk538kl40vl5ec58jmf1txkmity FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);

ALTER TABLE grade
    ADD CONSTRAINT fk5s1gpik3vodxr5cyqkyn6mkek FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);

ALTER TABLE grade
    ADD CONSTRAINT fk7e8ca7hfmrpruicqhocskjlf2 FOREIGN KEY (course_id) REFERENCES course(id);

ALTER TABLE renewed_expelled_student
    ADD CONSTRAINT fk8i4ftbqmtllto4rwrehaltsdx FOREIGN KEY (student_group_id) REFERENCES student_group(id);

ALTER TABLE student_degree
    ADD CONSTRAINT fk9st6a1j5cw6s3xkakvnavyi99 FOREIGN KEY (degree_id) REFERENCES degree(id);

ALTER TABLE course
    ADD CONSTRAINT fkcurovqow7p3irfphx9ucpx5lr FOREIGN KEY (course_name_id) REFERENCES course_name(id);

ALTER TABLE student_academic_vacation
    ADD CONSTRAINT fke3qe359u8kbkfaim28vw97758 FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);

ALTER TABLE student_academic_vacation
    ADD CONSTRAINT fke6j5q4pv8onchxlkq2m7282ey FOREIGN KEY (student_group_id) REFERENCES student_group(id);

ALTER TABLE student_expel
    ADD CONSTRAINT fkfkdbusap38npuwhdye6pg99hy FOREIGN KEY (student_group_id) REFERENCES student_group(id);

ALTER TABLE student_degree
    ADD CONSTRAINT fkgy7f1su2qykfrx8b3snqlssek FOREIGN KEY (student_group_id) REFERENCES student_group(id);

ALTER TABLE student_degree
    ADD CONSTRAINT fkj1llwoe8g31ishojdh9xv44t8 FOREIGN KEY (specialization_id) REFERENCES specialization(id);

ALTER TABLE specialization
    ADD CONSTRAINT fkjbku080fy3i9fbr82lccuit2h FOREIGN KEY (speciality_id) REFERENCES speciality(id);

ALTER TABLE teacher
    ADD CONSTRAINT fkjdcmjbru2mdniqr8rk6phwx6e FOREIGN KEY (department_id) REFERENCES department(id);

ALTER TABLE specialization
    ADD CONSTRAINT fkjk07jlkbyg2dnhqlhd6udqyld FOREIGN KEY (faculty_id) REFERENCES faculty(id);

ALTER TABLE student_expel
    ADD CONSTRAINT fkjn4uf1ps2my5e9poueo8rvook FOREIGN KEY (order_reason_id) REFERENCES order_reason(id);

ALTER TABLE specialization
    ADD CONSTRAINT fkjnv6iiajjgw8x2e55shgj5y8h FOREIGN KEY (degree_id) REFERENCES degree(id);

ALTER TABLE student_degree
    ADD CONSTRAINT fkl2otuowdej7vkvfxbi2tq5jgd FOREIGN KEY (student_id) REFERENCES student(id);

ALTER TABLE student
    ADD CONSTRAINT fkndn9m434jroav4tpm0tvo0vkk FOREIGN KEY (privilege_id) REFERENCES privilege(id);

ALTER TABLE renewed_academic_vacation_student
    ADD CONSTRAINT fknld8adwhu2yp51sdf3tk8j92f FOREIGN KEY (student_group_id) REFERENCES student_group(id);

ALTER TABLE specialization
    ADD CONSTRAINT fkq2c76cyld8gltdkxod8fosvub FOREIGN KEY (department_id) REFERENCES department(id);

ALTER TABLE renewed_academic_vacation_student
    ADD CONSTRAINT fkq630ubwk4twmf2y6rh0kui1n8 FOREIGN KEY (student_academic_vacation_id) REFERENCES student_academic_vacation(id);

ALTER TABLE student_group
    ADD CONSTRAINT fkro5caukwd9ufot4qqkdqcbhx6 FOREIGN KEY (specialization_id) REFERENCES specialization(id);

ALTER TABLE courses_for_groups
    ADD CONSTRAINT fkrwsolvxclmfpwhunt70mrlq7s FOREIGN KEY (student_group_id) REFERENCES student_group(id);

ALTER TABLE renewed_expelled_student
    ADD CONSTRAINT fksc5pauvj5kxci1w5o4ymvpsa5 FOREIGN KEY (student_expel_id) REFERENCES student_expel(id);

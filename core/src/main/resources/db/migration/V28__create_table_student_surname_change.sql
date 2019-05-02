CREATE TABLE student_surname_change
(
  id                   SERIAL PRIMARY KEY,
  order_date           date         NOT NULL,
  order_number         varchar(10)  NOT NULL,
  faculty_id           integer      NOT NULL,
  faculty_name         varchar(100),
  student_degree_id    integer      NOT NULL,
  surname_change_date  date         NOT NULL,
  speciality_name      varchar(100) NOT NULL,
  specialization_name  varchar(100) NOT NULL,
  student_year         integer,
  student_group_name   varchar(25)  NOT NULL,
  tuition_form         varchar(10)  NOT NULL,
  payment              varchar(8)   NOT NULL,
  application_date     date         NOT NULL,
  application_based_on varchar(255) NOT NULL,
  old_surname          varchar(25)  NOT NULL,
  new_surname          varchar(25)  NOT NULL
);

ALTER TABLE student_surname_change
    ADD CONSTRAINT uk_student_surname_change_student_degree_id_AND_order_date UNIQUE (student_degree_id, order_date);

ALTER TABLE student_surname_change
    ADD CONSTRAINT uk_student_surname_change_student_degree_id_AND_order_number UNIQUE (student_degree_id, order_number);

ALTER TABLE student_surname_change
    ADD CONSTRAINT FK_student_surname_change_Faculty_id_TO_faculty FOREIGN KEY (faculty_id) REFERENCES faculty;

ALTER TABLE student_surname_change
    ADD CONSTRAINT FK_student_surname_change_Student_degree_id_TO_student FOREIGN KEY (student_degree_id) REFERENCES student_degree;


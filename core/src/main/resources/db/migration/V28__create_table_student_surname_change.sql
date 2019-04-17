CREATE TABLE student_surname_change
(
  id                   integer      NOT NULL,
  order_date           date         NOT NULL,
  order_number         varchar(10)  NOT NULL,
  faculty_id           integer      NOT NULL,
  student_degree_id    integer      NOT NULL,
  surname_change_date  date         NOT NULL,
  speciality_name      varchar(25)  NOT NULL,
  specialization_name  varchar(25)  NOT NULL,
  student_year         integer      NOT NULL,
  student_group_name   varchar(100) NOT NULL,
  tuition_form         varchar(10)  NOT NULL,
  payment              varchar(8)   NOT NULL,
  application_date     date         NOT NULL,
  application_based_on varchar(255) NOT NULL,
  old_surname          varchar(25)  NOT NULL,
  new_surname          varchar(25)  NOT NULL,

  primary key (id)
);

alter table student_surname_change
  add constraint uk_student_surname_change_student_degree_id_AND_order_date UNIQUE (student_degree_id, order_date);

alter table student_surname_change
  add constraint uk_student_surname_change_student_degree_id_AND_order_number UNIQUE (student_degree_id, order_number);

alter table student_surname_change
  add constraint FK_student_surname_change_Faculty_id_TO_faculty foreign key (faculty_id) references faculty;

alter table student_surname_change
  add constraint FK_student_surname_change_Student_degree_id_TO_student foreign key (student_degree_id) references student_degree;

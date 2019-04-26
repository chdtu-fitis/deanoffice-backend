--
-- Name: student_surname_change_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_surname_change_id_seq
  START 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

CREATE TABLE student_surname_change
(
  --     id                   SERIAL PRIMARY KEY,
  id                   integer      NOT NULL DEFAULT nextval('student_surname_change_id_seq'),
  order_date           date         NOT NULL,
  order_number         varchar(10)  NOT NULL,
  faculty_id           integer      NOT NULL,
  faculty_name         varchar(100),
  student_degree_id    integer      NOT NULL,
  surname_change_date  date         NOT NULL,
  speciality_name      varchar(100) NOT NULL,
  specialization_name  varchar(100)  NOT NULL,
  student_year         integer,
  student_group_name   varchar(25)  NOT NULL,
  tuition_form         varchar(10)  NOT NULL,
  payment              varchar(8)   NOT NULL,
  application_date     date         NOT NULL,
  application_based_on varchar(255) NOT NULL,
  old_surname          varchar(25)  NOT NULL,
  new_surname          varchar(25)  NOT NULL
);

--
-- Name: student_surname_change_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE student_surname_change_id_seq OWNED BY student_surname_change.id;

alter table student_surname_change
  add constraint uk_student_surname_change_student_degree_id_AND_order_date UNIQUE (student_degree_id, order_date);

alter table student_surname_change
  add constraint uk_student_surname_change_student_degree_id_AND_order_number UNIQUE (student_degree_id, order_number);

alter table student_surname_change
  add constraint FK_student_surname_change_Faculty_id_TO_faculty foreign key (faculty_id) references faculty;

alter table student_surname_change
  add constraint FK_student_surname_change_Student_degree_id_TO_student foreign key (student_degree_id) references student_degree;


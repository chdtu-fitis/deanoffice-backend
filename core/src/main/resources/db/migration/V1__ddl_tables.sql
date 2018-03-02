/*create table course
(
	id serial not null
		constraint course_pkey
			primary key,
	credits numeric(4,1) not null,
	hours integer not null,
	semester integer not null,
	coursename_id integer,
	kc_id integer
)
;

create table course_name
(
	id serial not null
		constraint coursename_pkey
			primary key,
	name varchar(100) not null,
	name_eng varchar(100),
	abbreviation varchar(15)
)
;

alter table course
	add constraint fk7a99giwuwqju96i92m9gw9mk3
		foreign key (coursename_id) references course_name
;

create table courses_for_groups
(
	id serial not null
		constraint courses_for_groups_pkey
			primary key,
	exam_date timestamp,
	course_id integer
		constraint fkfqxp678s005clfsc3ocusfqme
			references course,
	studentgroup_id integer,
	teacher_id integer,
	constraint ukacyne4h2elppn968u5prjg2rd
		unique (course_id, studentgroup_id)
)
;

create table current_year
(
	id serial not null
		constraint current_year_pkey
			primary key,
	curr_year integer
)
;

create table degree
(
	id serial not null
		constraint degree_pkey
			primary key,
	name varchar(100) not null,
	name_eng varchar(100),
	constraint ukhsy90ut35sobbhgmb5sb5eh7
		unique (id, name)
)
;

create table department
(
	id serial not null
		constraint department_pkey
			primary key,
	name varchar(100) not null,
	active boolean not null,
	abbr varchar(20) not null,
	faculty_id integer
)
;

create table faculty
(
	id serial not null
		constraint faculty_pkey
			primary key,
	name varchar(100) not null,
	name_eng varchar(100),
	active boolean not null,
	abbr varchar(20) not null
		constraint uk_757mlj4kyjn7mpb0fb67owj00
			unique
)
;

alter table department
	add constraint fk41ox44lg0tyunaxq4y8oyuq8m
		foreign key (faculty_id) references faculty
;

create table grade
(
	id serial not null
		constraint grade_pkey
			primary key,
	ects varchar(2) not null,
	grade integer not null,
	points integer not null,
	course_id integer
		constraint fko9djxg4taceuo4s4gyi63kmpf
			references course,
	student_id integer,
	constraint ukm4rb6f2efyvns11hpghb8qilv
		unique (course_id, student_id)
)
;

create table knowledge_control
(
	id serial not null
		constraint knowledge_control_pkey
			primary key,
	name varchar(100) not null,
	name_eng varchar(100),
	has_grade boolean not null
)
;

alter table course
	add constraint fk2mi63y0iy38x5tkd3ms6s9qsa
		foreign key (kc_id) references knowledge_control
;

create table order_reason
(
	id serial not null
		constraint order_reason_pkey
			primary key,
	name varchar(100) not null,
	active boolean not null,
	kind varchar(25) not null
)
;

create table position
(
	id serial not null
		constraint position_pkey
			primary key,
	name varchar(100) not null
)
;

create table privilege
(
	id serial not null
		constraint privilege_pkey
			primary key,
	name varchar(100) not null,
	active boolean not null
)
;

create table speciality
(
	id serial not null
		constraint speciality_pkey
			primary key,
	name varchar(100) not null,
	name_eng varchar(100),
	active boolean not null,
	code varchar(20) not null
		constraint uk_f7cgfesjcj3ygekov90nlvnaa
			unique
)
;

create table specialization
(
	id serial not null
		constraint specialization_pkey
			primary key,
	name varchar(100) not null,
	name_eng varchar(100),
	active boolean not null,
	payment_extramural numeric(15,2),
	payment_fulltime numeric(15,2),
	qualification varchar(100),
	qualification_eng varchar(100),
	degree_id integer
		constraint fkjnv6iiajjgw8x2e55shgj5y8h
			references degree,
	department_id integer
		constraint fkq2c76cyld8gltdkxod8fosvub
			references department,
	faculty_id integer
		constraint fkjk07jlkbyg2dnhqlhd6udqyld
			references faculty,
	speciality_id integer
		constraint fkjbku080fy3i9fbr82lccuit2h
			references speciality
)
;

create table student
(
	id serial not null
		constraint student_pkey
			primary key,
	active boolean not null,
	name varchar(20) not null,
	patronimic varchar(20) not null,
	sex char not null,
	surname varchar(20) not null,
	actual_address varchar(100),
	birth_date timestamp,
	email varchar(30),
	father_info varchar(70),
	father_name varchar(40),
	father_phone varchar(20),
	mother_info varchar(70),
	mother_name varchar(40),
	mother_phone varchar(20),
	name_eng varchar(20),
	notes varchar(150),
	patronimic_eng varchar(20),
	record_book_number varchar(15),
	registration_address varchar(100),
	school varchar(100),
	student_card_number varchar(15),
	surname_eng varchar(20),
	telephone varchar(30),
	privilege_id integer
		constraint fki3pujilwebvr6affpudisi13c
			references privilege,
	studentgroup_id integer
)
;

alter table grade
	add constraint fkjrrnskqj2brxn6kpc2vy197iq
		foreign key (student_id) references student
;

create table student_academic_vacation
(
	id serial not null
		constraint student_academic_vacation_pkey
			primary key,
	application_date timestamp not null,
	order_date timestamp not null,
	order_number varchar(15) not null,
	vacation_end_date timestamp not null,
	vacation_start_date timestamp not null,
	group_id integer,
	reason_id integer
		constraint fklaj0en91yngimnh4mnesmnmyq
			references order_reason,
	student_id integer
		constraint fkdny9x31pqu94q8oij8q1ahy5j
			references student
)
;

create table student_expel
(
	id serial not null
		constraint student_expel_pkey
			primary key,
	application_date timestamp not null,
	expel_date timestamp not null,
	order_date timestamp not null,
	order_number varchar(15) not null,
	group_id integer,
	reason_id integer
		constraint fk12i9855ocnh9jj457rloqfeql
			references order_reason,
	student_id integer
		constraint fknk6bvig1wgl9sbthqj3qwfybb
			references student
)
;

create table student_group
(
	id serial not null
		constraint studentgroup_pkey
			primary key,
	name varchar(100) not null,
	active boolean not null,
	begin_years integer not null,
	creation_year integer not null,
	study_semesters integer not null,
	study_years numeric(19,2) not null,
	tuition_form char not null,
	tuition_term char not null,
	specialization_id integer
		constraint fkl26v2ydarx9ec7qchv98xqkcq
			references specialization
)
;

alter table courses_for_groups
	add constraint fkjshxsn3dotdm41ynujr4pwp3x
		foreign key (studentgroup_id) references student_group
;

alter table student
	add constraint fkamc5s7rcd0jaygjbknjffflba
		foreign key (studentgroup_id) references student_group
;

alter table student_academic_vacation
	add constraint fk17xdeg7avnqjn0muw4hqegejr
		foreign key (group_id) references student_group
;

alter table student_expel
	add constraint fkrqvkc3r60nv4phchsnjge6tb9
		foreign key (group_id) references student_group
;

create table teacher
(
	id serial not null
		constraint teacher_pkey
			primary key,
	active boolean not null,
	name varchar(20) not null,
	patronimic varchar(20) not null,
	sex char not null,
	surname varchar(20) not null,
	scientific_degree varchar(255),
	department_id integer
		constraint fkjdcmjbru2mdniqr8rk6phwx6e
			references department,
	position_id integer
		constraint fk3ebf13bw5jxpbch977xtuxrax
			references position
)
;

alter table courses_for_groups
	add constraint fk1t8vjy9y1j7cf7s5dorx1t1it
		foreign key (teacher_id) references teacher
;

INSERT INTO public.degree (name, name_eng) VALUES ('Бакалавр', 'Bachelor');
INSERT INTO public.degree (name, name_eng) VALUES ('Спеціаліст', 'Specialist');
INSERT INTO public.degree (name, name_eng) VALUES ('Магістр', 'Master');

INSERT INTO public.knowledge_control (name, name_eng, has_grade) VALUES ('іспит', 'exam', true);
INSERT INTO public.knowledge_control (name, name_eng, has_grade) VALUES ('залік', 'credit', false);
INSERT INTO public.knowledge_control (name, name_eng, has_grade) VALUES ('курсова робота', 'course work', true);
INSERT INTO public.knowledge_control (name, name_eng, has_grade) VALUES ('курсовий проект', 'course project', true);
INSERT INTO public.knowledge_control (name, name_eng, has_grade) VALUES ('диференційований залік', 'differentiated credit', true);
INSERT INTO public.knowledge_control (name, name_eng, has_grade) VALUES ('державний іспит', 'state exam', true);
INSERT INTO public.knowledge_control (name, name_eng, has_grade) VALUES ('атестація', 'attestation', true);
INSERT INTO public.knowledge_control (name, name_eng, has_grade) VALUES ('практика', 'internship', true);
*/
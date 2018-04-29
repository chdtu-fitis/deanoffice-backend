create table application_user
(
	id bigserial not null,
	first_name varchar(255) not null,
	last_name varchar(2555) not null,
	password varchar(255) not null,
	username varchar(255),
	faculty_id integer
		constraint application_user_faculty_id_fk
			references faculty
)
;

create unique index application_user_id_uindex
	on application_user (id)
;

create unique index application_user_username_uindex
	on application_user (username)
;

INSERT INTO public.application_user (id, first_name, last_name, password, username, faculty_id) VALUES (3, 'admin', 'admin', 'test', 'test', 1);

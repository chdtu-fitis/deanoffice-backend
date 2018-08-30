create table application_user
(
	id integer not null,
	first_name varchar(255) not null,
	last_name varchar(255) not null,
	password varchar(255) not null,
	username varchar(255),
	faculty_id integer
		constraint application_user_faculty_id_fk
			references faculty
)
;

CREATE SEQUENCE application_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY application_user
    ADD CONSTRAINT application_user_pkey PRIMARY KEY (id);


ALTER TABLE ONLY application_user ALTER COLUMN id SET DEFAULT nextval('application_user_id_seq'::regclass);

create unique index application_user_id_uindex
	on application_user (id)
;

create unique index application_user_username_uindex
	on application_user (username)
;

INSERT INTO public.application_user (first_name, last_name, password, username, faculty_id) VALUES ('admin', 'admin', 'test', 'test', 1);

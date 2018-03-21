create table application_user
(
	id bigserial not null,
	first_name varchar(255) not null,
	last_name varchar(2555) not null,
	password varchar(255) not null,
	username varchar(255)
)
;

create unique index application_user_id_uindex
	on application_user (id)
;

INSERT INTO application_user (id, first_name, last_name, password, username) VALUES (3, 'admin', 'admin', 'test', 'test');
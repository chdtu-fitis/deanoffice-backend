CREATE TABLE application_user(
	id SERIAL PRIMARY KEY,
	first_name varchar(255) NOT NULL,
	last_name varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	username varchar(255),
	faculty_id integer
);

ALTER TABLE application_user ADD CONSTRAINT application_user_faculty_id_fk FOREIGN KEY (faculty_id) REFERENCES faculty(id);

CREATE UNIQUE index application_user_id_uindex ON application_user (id);

CREATE UNIQUE index application_user_username_uindex ON application_user (username);

INSERT INTO application_user (first_name, last_name, password, username, faculty_id) VALUES ('admin', 'admin', 'test', 'test', 1);

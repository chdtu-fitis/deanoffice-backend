create table roles(
  id SERIAL primary key,
  name varchar not null
);

ALTER TABLE roles ADD CONSTRAINT uk_roles_name UNIQUE (name);

create table users_roles(
  id SERIAL primary key,
  user_id integer NOT NULL,
  role_id integer NOT NULL
);

ALTER TABLE users_roles ADD CONSTRAINT uk_users_roles UNIQUE (user_id, role_id);

ALTER TABLE users_roles ADD CONSTRAINT fk_users_roles_users FOREIGN KEY (user_id) REFERENCES application_user(id);

ALTER TABLE users_roles ADD CONSTRAINT fk_users_roles_roles FOREIGN KEY (role_id) REFERENCES roles(id);

INSERT INTO roles(name) VALUES ('ROLE_ADMIN');
INSERT INTO roles(name) VALUES ('ROLE_METHOD');
INSERT INTO roles(name) VALUES ('ROLE_DEANOFFICER');

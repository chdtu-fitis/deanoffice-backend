CREATE TABLE acquired_competencies (
    id SERIAL PRIMARY KEY,
    competencies character varying(8500) NOT NULL,
    competencies_eng character varying(8500) NOT NULL,
    year integer NOT NULL,
    specialization_id integer NOT NULL
);

CREATE TABLE professional_qualification (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    name_eng character varying(100),
    code character varying(10) NOT NULL
);

CREATE TABLE qualifications_for_specializations (
    id SERIAL PRIMARY KEY,
    year integer NOT NULL,
    professional_qualification_id integer NOT NULL,
    specialization_id integer NOT NULL
);

ALTER TABLE acquired_competencies
    ADD CONSTRAINT ukjvpyl6attc5vs0sya4xgdkgv3 UNIQUE (specialization_id, year);

ALTER TABLE qualifications_for_specializations
    ADD CONSTRAINT ukn25tsk36xg9t1i29fg9irthme UNIQUE (professional_qualification_id, specialization_id, year);

ALTER TABLE acquired_competencies
    ADD CONSTRAINT fkf87brq3rjnag7vkqqdo1bdx7n FOREIGN KEY (specialization_id) REFERENCES specialization (id);

ALTER TABLE qualifications_for_specializations
    ADD CONSTRAINT fko4pe5sogqngvm1wrfhfq7pyc0 FOREIGN KEY (professional_qualification_id) REFERENCES professional_qualification(id);

ALTER TABLE qualifications_for_specializations
    ADD CONSTRAINT fk21tpfc5ga11nhglnnlltlehaj FOREIGN KEY (specialization_id) REFERENCES specialization (id);

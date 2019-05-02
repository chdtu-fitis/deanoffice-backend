CREATE TABLE student_transfer (
    id SERIAL PRIMARY KEY,
    order_date date NOT NULL,
    order_number character varying(15) NOT NULL,
    old_study_year integer NOT NULL,
    new_study_year integer NOT NULL,
    old_specialization_id integer NOT NULL,
    new_specialization_id integer NOT NULL,
    old_payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    new_payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    application_date date NOT NULL,
    contract_number character varying(15),
    contract_date date,
    student_degree_id integer NOT NULL,
    old_student_group_id integer NOT NULL,
    new_student_group_id integer NOT NULL
);

ALTER TABLE student_transfer
    ADD CONSTRAINT fk_student_transfer_old_specialization_id FOREIGN KEY (old_specialization_id) REFERENCES specialization(id);

ALTER TABLE student_transfer
    ADD CONSTRAINT fk_student_transfer_new_specialization_id FOREIGN KEY (new_specialization_id) REFERENCES specialization(id);

ALTER TABLE student_transfer
    ADD CONSTRAINT fk_student_transfer_student_degree_id FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);

ALTER TABLE student_transfer
    ADD CONSTRAINT fk_student_transfer_old_group_id FOREIGN KEY (old_student_group_id) REFERENCES student_group(id);

ALTER TABLE student_transfer
    ADD CONSTRAINT fk_student_transfer_new_group_id FOREIGN KEY (new_student_group_id) REFERENCES student_group(id);

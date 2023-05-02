CREATE TABLE student_previous_university (
    id integer NOT NULL,
    student_degree_id integer NOT NULL,
    university_name character varying(150) NOT NULL,
    study_start_date date NOT NULL,
    study_end_date date NOT NULL,
    academic_certificate_number character varying(15),
    academic_certificate_date date
);

CREATE SEQUENCE student_previous_university_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY student_previous_university ALTER COLUMN id SET DEFAULT nextval('student_previous_university_id_seq'::regclass);

ALTER TABLE ONLY student_previous_university ADD CONSTRAINT student_previous_university_pkey PRIMARY KEY (id);

ALTER TABLE ONLY student_previous_university ADD CONSTRAINT ukvtr97cwy234e4ll4g11u2dtb1z UNIQUE (student_degree_id, study_start_date);

ALTER TABLE ONLY student_previous_university
    ADD CONSTRAINT fkq768ubwr4vwmf2y6rh0kdf1n9 FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);

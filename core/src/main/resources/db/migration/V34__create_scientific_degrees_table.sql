CREATE TABLE scientific_degree (
    id                   SERIAL PRIMARY KEY,
	name VARCHAR(50)     NOT NULL,
    name_eng VARCHAR(50) NULL DEFAULT NULL,
    abbr VARCHAR(15)     NOT NULL
);



ALTER SEQUENCE scientific_degree_id_seq OWNED BY scientific_degree.id;
ALTER TABLE ONLY scientific_degree ALTER COLUMN id SET DEFAULT nextval('scientific_degree_id_seq'::regclass);

ALTER TABLE teacher
ADD COLUMN academic_title VARCHAR(25);
ALTER TABLE teacher
ADD COLUMN academic_title_eng VARCHAR(25);
ALTER TABLE teacher
ADD COLUMN scientific_degree_id INTEGER;

ALTER TABLE teacher
ADD CONSTRAINT FK_teacher_scientific_degree FOREIGN KEY (scientific_degree_id) references scientific_degree;
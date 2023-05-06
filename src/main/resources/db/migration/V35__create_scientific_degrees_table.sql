CREATE TABLE scientific_degree (
    id                   SERIAL PRIMARY KEY,
	name VARCHAR(50)     NOT NULL,
    name_eng VARCHAR(50) NOT NULL DEFAULT '',
    abbr VARCHAR(15)     NOT NULL
);

ALTER TABLE scientific_degree ADD CONSTRAINT uk_scientific_degree_name UNIQUE (name);
-- ALTER SEQUENCE scientific_degree_id_seq OWNED BY scientific_degree.id;
-- ALTER TABLE ONLY scientific_degree ALTER COLUMN id SET DEFAULT nextval('scientific_degree_id_seq'::regclass);
INSERT INTO scientific_degree(name, abbr, name_eng) VALUES ('доктор технічних наук', 'д.т.н.', 'Doctor of Technical Science');
INSERT INTO scientific_degree(name, abbr, name_eng) VALUES ('доктор фізико-математичних наук', 'д.ф-м.н.', 'Doctor of Physical-Mathematical Science');
INSERT INTO scientific_degree(name, abbr, name_eng) VALUES ('доктор хіміних наук', 'д.т.н.', 'Doctor of Chemical Science');

ALTER TABLE teacher ADD COLUMN academic_title VARCHAR(25);
ALTER TABLE teacher ADD COLUMN scientific_degree_id INTEGER;
ALTER TABLE teacher DROP COLUMN scientific_degree;

ALTER TABLE teacher
  ADD CONSTRAINT FK_teacher_scientific_degree FOREIGN KEY (scientific_degree_id) references scientific_degree;

ALTER TABLE specialization DROP COLUMN applying_knowledge_and_understanding_outcomes;
ALTER TABLE specialization DROP COLUMN applying_knowledge_and_understanding_outcomes_eng;
ALTER TABLE specialization DROP COLUMN knowledge_and_understanding_outcomes;
ALTER TABLE specialization DROP COLUMN knowledge_and_understanding_outcomes_eng;
ALTER TABLE specialization DROP COLUMN making_judgements_outcomes_eng;
ALTER TABLE specialization DROP COLUMN making_judgements_outcomes;

ALTER TABLE specialization ADD COLUMN program_head_id INTEGER;
ALTER TABLE specialization
  ADD CONSTRAINT FK_teacher_program_head FOREIGN KEY (program_head_id) references teacher;

ALTER TABLE speciality ADD COLUMN name_genitive VARCHAR(100) NOT NULL DEFAULT '';
ALTER TABLE speciality ADD COLUMN field_of_knowledge_id INTEGER NOT NULL DEFAULT 7;
ALTER TABLE speciality
  ADD CONSTRAINT FK_speciality_field_of_knowledge FOREIGN KEY (field_of_knowledge_id) references field_of_knowledge;

ALTER TABLE field_of_knowledge ADD COLUMN code_international VARCHAR(20) NOT NULL DEFAULT '';
ALTER TABLE field_of_knowledge ADD COLUMN name_international VARCHAR(100) NOT NULL DEFAULT '';

ALTER TABLE degree ADD COLUMN national_qualification_framework_level VARCHAR(250) NOT NULL DEFAULT '';
ALTER TABLE degree ADD COLUMN national_qualification_framework_level_eng VARCHAR(250) NOT NULL DEFAULT '';

ALTER TABLE specialization ADD COLUMN certificate_issued_by VARCHAR(100) NOT NULL DEFAULT '';
ALTER TABLE specialization ADD COLUMN certificate_issued_by_eng VARCHAR(100) NOT NULL DEFAULT '';

ALTER TABLE department ADD COLUMN web_site VARCHAR(120) NOT NULL DEFAULT '';

INSERT INTO field_of_knowledge (code, name, name_eng) VALUES
	('11', 'Математика та статистика', 'Mathematics and Statistics'),
	('20', 'Аграрні науки та продовольство', 'Agrarian Science and Food');


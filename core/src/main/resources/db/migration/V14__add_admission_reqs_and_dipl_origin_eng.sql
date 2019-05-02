ALTER TABLE degree
  ADD COLUMN admission_foreign_requirements character varying(255);

ALTER TABLE degree
  ADD COLUMN admission_foreign_requirements_eng character varying(255);

ALTER TABLE degree
  ADD COLUMN admission_shortened_requirements character varying(255);

ALTER TABLE degree
  ADD COLUMN admission_shortened_requirements_eng character varying(255);

ALTER TABLE student_degree
  ADD COLUMN previous_diploma_issued_by_eng character varying(255);
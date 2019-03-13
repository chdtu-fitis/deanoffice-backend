ALTER TABLE PUBLIC.courses_for_groups
  ADD COLUMN academic_difference boolean DEFAULT FALSE NOT NULL;
ALTER TABLE PUBLIC.grade
  ADD COLUMN academic_difference boolean DEFAULT FALSE NOT NULL;
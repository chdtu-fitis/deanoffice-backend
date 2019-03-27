ALTER TABLE courses_for_groups
  ADD COLUMN academic_difference boolean DEFAULT FALSE NOT NULL;
ALTER TABLE grade
  ADD COLUMN academic_difference boolean DEFAULT FALSE NOT NULL;
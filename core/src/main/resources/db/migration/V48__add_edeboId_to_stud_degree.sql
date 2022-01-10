ALTER TABLE student_degree ADD COLUMN edebo_id VARCHAR(15);
UPDATE student_degree SET edebo_id = supplement_number WHERE supplement_number IS NOT NULL;
UPDATE student_degree SET supplement_number = NULL WHERE active = true;

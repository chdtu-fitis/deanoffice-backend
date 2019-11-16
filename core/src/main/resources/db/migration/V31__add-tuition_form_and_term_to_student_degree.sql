ALTER TABLE student_degree
ADD COLUMN tuition_form character varying (10);
ALTER TABLE student_degree
ADD COLUMN tuition_term character varying (10);

UPDATE student_degree
SET tuition_form = student_group.tuition_form,
    tuition_term = student_group.tuition_term
FROM student_group
WHERE student_group.id = student_degree.student_group_id;

UPDATE student_degree SET tuition_form ='FULL_TIME'
WHERE tuition_form IS NULL;
UPDATE student_degree SET tuition_term = 'REGULAR'
WHERE tuition_term IS NULL;

ALTER TABLE student_degree
ALTER COLUMN tuition_form SET NOT NULL ;
ALTER TABLE student_degree
ALTER COLUMN tuition_term SET NOT NULL;
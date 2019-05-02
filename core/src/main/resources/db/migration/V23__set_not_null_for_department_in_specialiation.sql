UPDATE specialization SET department_id=1 WHERE department_id IS NULL;

ALTER TABLE specialization ALTER COLUMN department_id SET NOT NULL;
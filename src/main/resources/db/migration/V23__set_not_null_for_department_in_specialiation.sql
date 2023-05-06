UPDATE specialization set department_id=1 where department_id is null;
ALTER TABLE specialization ALTER COLUMN department_id set NOT NULL;

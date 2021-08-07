ALTER TABLE faculty ADD COLUMN genitive_case VARCHAR(100) NOT NULL DEFAULT '';

UPDATE faculty SET genitive_case = 'факультету інформаційних технологій і систем' WHERE id = 1;
UPDATE faculty SET genitive_case = 'факультету економіки та управління' WHERE id = 2;
UPDATE faculty SET genitive_case = 'будівельного факультету' WHERE id = 9;

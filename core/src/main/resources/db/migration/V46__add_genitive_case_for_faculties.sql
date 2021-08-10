ALTER TABLE faculty ADD COLUMN name_genitive VARCHAR(100) NOT NULL DEFAULT '';

UPDATE faculty SET name_genitive = 'факультету інформаційних технологій і систем' WHERE id = 1;
UPDATE faculty SET name_genitive = 'факультету економіки та управління' WHERE id = 2;
UPDATE faculty SET name_genitive = 'факультету гуманітарних технологій' WHERE id = 4;
UPDATE faculty SET name_genitive = 'факультету комп''ютеризованих технологій машинобудування і дизайну' WHERE id = 5;
UPDATE faculty SET name_genitive = 'факультету електронних технологій і робототехніки' WHERE id = 7;
UPDATE faculty SET name_genitive = 'будівельного факультету' WHERE id = 9;

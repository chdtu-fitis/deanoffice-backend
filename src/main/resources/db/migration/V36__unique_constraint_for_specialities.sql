ALTER TABLE speciality ADD CONSTRAINT uk_speciality_name_and_code UNIQUE (name, code);

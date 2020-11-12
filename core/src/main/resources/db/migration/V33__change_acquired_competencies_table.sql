ALTER TABLE acquired_competencies
DROP COLUMN competencies_eng;

ALTER TABLE acquired_competencies
RENAME COLUMN competencies_ukr TO competencies;

ALTER TABLE  acquired_competencies
DROP CONSTRAINT ukjvpyl6attc5vs0sya4xgdkgv3;
ALTER TABLE acquired_competencies
ADD CONSTRAINT uk_acquired_competencies_year_specialization_id UNIQUE (year, specialization_id);

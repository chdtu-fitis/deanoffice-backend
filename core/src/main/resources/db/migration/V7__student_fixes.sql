ALTER TABLE student ALTER COLUMN photo type character varying(250);
ALTER TABLE student rename column photo TO photo_url;

ALTER TABLE renewed_expelled_student
  ADD COLUMN order_date date default '1980-01-01' NOT NULL;

ALTER TABLE renewed_expelled_student
  ADD COLUMN order_number character varying(15) DEFAULT ''::character varying NOT NULL;

ALTER TABLE renewed_academic_vacation_student
  ADD COLUMN order_date date default '1980-01-01'  NOT NULL;

ALTER TABLE renewed_academic_vacation_student
  ADD COLUMN order_number character varying(15) DEFAULT ''::character varying NOT NULL;

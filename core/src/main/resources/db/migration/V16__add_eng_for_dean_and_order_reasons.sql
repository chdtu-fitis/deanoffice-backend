ALTER TABLE order_reason
    ADD COLUMN name_eng character varying(100) DEFAULT '' NOT NULL;

ALTER TABLE faculty
    ADD COLUMN dean_eng character varying(70) DEFAULT '' NOT NULL;

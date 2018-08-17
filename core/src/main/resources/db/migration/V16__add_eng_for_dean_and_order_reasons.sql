alter table public.order_reason
  add column name_eng character varying(100) DEFAULT '' NOT NULL;
alter table public.faculty
  add column dean_eng character varying(70) DEFAULT '' NOT NULL;
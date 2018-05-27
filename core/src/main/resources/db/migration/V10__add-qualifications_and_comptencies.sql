
-- Table: public.acquired_competencies

CREATE SEQUENCE public.acquired_competencies_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE public.acquired_competencies_id_seq
    OWNER TO postgres;

CREATE TABLE public.acquired_competencies
(
    id integer NOT NULL DEFAULT nextval('acquired_competencies_id_seq'::regclass),
    competencies character varying(8500) COLLATE pg_catalog."default" NOT NULL,
    competencies_eng character varying(8500) COLLATE pg_catalog."default" NOT NULL,
    year integer NOT NULL,
    specialization_id integer NOT NULL,
    CONSTRAINT acquired_competencies_pkey PRIMARY KEY (id),
    CONSTRAINT ukjvpyl6attc5vs0sya4xgdkgv3 UNIQUE (specialization_id, year),
    CONSTRAINT fkf87brq3rjnag7vkqqdo1bdx7n FOREIGN KEY (specialization_id)
        REFERENCES public.specialization (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.acquired_competencies
    OWNER to postgres;

-- Table: public.professional_qualification

CREATE SEQUENCE public.professional_qualification_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE public.professional_qualification_id_seq
    OWNER TO postgres;

CREATE TABLE public.professional_qualification
(
    id integer NOT NULL DEFAULT nextval('professional_qualification_id_seq'::regclass),
    name character varying(100) COLLATE pg_catalog."default" NOT NULL,
    name_eng character varying(100) COLLATE pg_catalog."default",
    code character varying(10) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT professional_qualification_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.professional_qualification
    OWNER to postgres;

-- Table: public.qualifications_for_specializations

CREATE SEQUENCE public.qualifications_for_specializations_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE public.qualifications_for_specializations_id_seq
    OWNER TO postgres;

CREATE TABLE public.qualifications_for_specializations
(
    id integer NOT NULL DEFAULT nextval('qualifications_for_specializations_id_seq'::regclass),
    year integer NOT NULL,
    professional_qualification_id integer NOT NULL,
    specialization_id integer NOT NULL,
    CONSTRAINT qualifications_for_specializations_pkey PRIMARY KEY (id),
    CONSTRAINT ukn25tsk36xg9t1i29fg9irthme UNIQUE (professional_qualification_id, specialization_id, year),
    CONSTRAINT fk21tpfc5ga11nhglnnlltlehaj FOREIGN KEY (specialization_id)
        REFERENCES public.specialization (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fko4pe5sogqngvm1wrfhfq7pyc0 FOREIGN KEY (professional_qualification_id)
        REFERENCES public.professional_qualification (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.qualifications_for_specializations
    OWNER to postgres;



ALTER TABLE public.specialization
ADD COLUMN certificate_date date NOT NULL DEFAULT '1980-01-01'::date,
ADD COLUMN certificate_number character varying(20) COLLATE pg_catalog."default" NOT NULL DEFAULT ''::character varying;
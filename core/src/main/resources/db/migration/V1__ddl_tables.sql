--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: course; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE course (
    id integer NOT NULL,
    credits numeric(4,1) NOT NULL,
    hours integer NOT NULL,
    semester integer NOT NULL,
    coursename_id integer,
    kc_id integer
);


ALTER TABLE course OWNER TO postgres;

--
-- Name: course_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE course_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE course_id_seq OWNER TO postgres;

--
-- Name: course_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE course_id_seq OWNED BY course.id;


--
-- Name: course_name; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE course_name (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    name_eng character varying(100) NOT NULL,
    abbreviation character varying(15)
);


ALTER TABLE course_name OWNER TO postgres;

--
-- Name: course_name_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE course_name_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE course_name_id_seq OWNER TO postgres;

--
-- Name: course_name_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE course_name_id_seq OWNED BY course_name.id;


--
-- Name: courses_for_groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE courses_for_groups (
    id integer NOT NULL,
    exam_date timestamp without time zone,
    course_id integer,
    studentgroup_id integer,
    teacher_id integer
);


ALTER TABLE courses_for_groups OWNER TO postgres;

--
-- Name: courses_for_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE courses_for_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE courses_for_groups_id_seq OWNER TO postgres;

--
-- Name: courses_for_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE courses_for_groups_id_seq OWNED BY courses_for_groups.id;


--
-- Name: current_year; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE current_year (
    id integer NOT NULL,
    curr_year integer
);


ALTER TABLE current_year OWNER TO postgres;

--
-- Name: current_year_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE current_year_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE current_year_id_seq OWNER TO postgres;

--
-- Name: current_year_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE current_year_id_seq OWNED BY current_year.id;


--
-- Name: degree; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE degree (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    name_eng character varying(100) NOT NULL,
    admission_requirements character varying(255),
    admission_requirements_eng character varying(255),
    further_study_access character varying(255),
    further_study_access_eng character varying(255),
    professional_status character varying(255),
    professional_status_eng character varying(255),
    qualification_level_descr character varying(255),
    qualification_level_descr_eng character varying(255)
);


ALTER TABLE degree OWNER TO postgres;

--
-- Name: degree_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE degree_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE degree_id_seq OWNER TO postgres;

--
-- Name: degree_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE degree_id_seq OWNED BY degree.id;


--
-- Name: department; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE department (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    active boolean NOT NULL,
    abbr character varying(20) NOT NULL,
    faculty_id integer
);


ALTER TABLE department OWNER TO postgres;

--
-- Name: department_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE department_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE department_id_seq OWNER TO postgres;

--
-- Name: department_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE department_id_seq OWNED BY department.id;


--
-- Name: faculty; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE faculty (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    name_eng character varying(100) NOT NULL,
    active boolean NOT NULL,
    abbr character varying(20) NOT NULL,
    dean character varying(70)
);


ALTER TABLE faculty OWNER TO postgres;

--
-- Name: faculty_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE faculty_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE faculty_id_seq OWNER TO postgres;

--
-- Name: faculty_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE faculty_id_seq OWNED BY faculty.id;


--
-- Name: grade; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE grade (
    id integer NOT NULL,
    ects character varying(2),
    grade integer NOT NULL,
    points integer NOT NULL,
    course_id integer,
    student_id integer
);


ALTER TABLE grade OWNER TO postgres;

--
-- Name: grade_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE grade_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE grade_id_seq OWNER TO postgres;

--
-- Name: grade_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE grade_id_seq OWNED BY grade.id;


--
-- Name: knowledge_control; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE knowledge_control (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    name_eng character varying(100) NOT NULL,
    has_grade boolean NOT NULL
);


ALTER TABLE knowledge_control OWNER TO postgres;

--
-- Name: knowledge_control_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE knowledge_control_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE knowledge_control_id_seq OWNER TO postgres;

--
-- Name: knowledge_control_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE knowledge_control_id_seq OWNED BY knowledge_control.id;


--
-- Name: order_reason; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE order_reason (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    active boolean NOT NULL,
    kind character varying(25) NOT NULL
);


ALTER TABLE order_reason OWNER TO postgres;

--
-- Name: order_reason_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE order_reason_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE order_reason_id_seq OWNER TO postgres;

--
-- Name: order_reason_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE order_reason_id_seq OWNED BY order_reason.id;


--
-- Name: position; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE "position" (
    id integer NOT NULL,
    name character varying(100) NOT NULL
);


ALTER TABLE "position" OWNER TO postgres;

--
-- Name: position_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE position_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE position_id_seq OWNER TO postgres;

--
-- Name: position_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE position_id_seq OWNED BY "position".id;


--
-- Name: privilege; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE privilege (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    active boolean NOT NULL
);


ALTER TABLE privilege OWNER TO postgres;

--
-- Name: privilege_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE privilege_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE privilege_id_seq OWNER TO postgres;

--
-- Name: privilege_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE privilege_id_seq OWNED BY privilege.id;


--
-- Name: schema_version; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schema_version (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE schema_version OWNER TO postgres;

--
-- Name: speciality; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE speciality (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    name_eng character varying(100) NOT NULL,
    active boolean NOT NULL,
    code character varying(20) NOT NULL,
    field_of_study character varying(255),
    field_of_study_eng character varying(255)
);


ALTER TABLE speciality OWNER TO postgres;

--
-- Name: speciality_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE speciality_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE speciality_id_seq OWNER TO postgres;

--
-- Name: speciality_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE speciality_id_seq OWNED BY speciality.id;


--
-- Name: specialization; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE specialization (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    name_eng character varying(100) NOT NULL,
    active boolean NOT NULL,
    applying_knowledge_and_understanding_outcomes character varying(1200),
    applying_knowledge_and_understanding_outcomes_eng character varying(1200),
    program_head_info character varying(255) NOT NULL,
    program_head_info_eng character varying(255) NOT NULL,
    program_head_name character varying(255) NOT NULL,
    program_head_name_eng character varying(255) NOT NULL,
    knowledge_and_understanding_outcomes character varying(1200),
    knowledge_and_understanding_outcomes_eng character varying(1200),
    making_judgements_outcomes character varying(1200),
    making_judgements_outcomes_eng character varying(1200),
    payment_extramural numeric(15,2),
    payment_fulltime numeric(15,2),
    qualification character varying(100),
    qualification_eng character varying(100),
    required_credits numeric(4,1),
    degree_id integer,
    department_id integer,
    faculty_id integer,
    speciality_id integer
);


ALTER TABLE specialization OWNER TO postgres;

--
-- Name: specialization_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE specialization_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE specialization_id_seq OWNER TO postgres;

--
-- Name: specialization_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE specialization_id_seq OWNED BY specialization.id;


--
-- Name: student; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE student (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    patronimic character varying(20) NOT NULL,
    sex character varying(6) DEFAULT 'MALE'::character varying NOT NULL,
    surname character varying(20) NOT NULL,
    actual_address character varying(100),
    birth_date date,
    email character varying(30),
    father_info character varying(70),
    father_name character varying(40),
    father_phone character varying(20),
    mother_info character varying(70),
    mother_name character varying(40),
    mother_phone character varying(20),
    name_eng character varying(20),
    notes character varying(150),
    patronimic_eng character varying(20),
    registration_address character varying(100),
    school character varying(100),
    student_card_number character varying(15),
    surname_eng character varying(20),
    telephone character varying(30),
    privilege_id integer
);


ALTER TABLE student OWNER TO postgres;

--
-- Name: student_academic_vacation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE student_academic_vacation (
    id integer NOT NULL,
    application_date date NOT NULL,
    order_date date NOT NULL,
    order_number character varying(15) NOT NULL,
    vacation_end_date date NOT NULL,
    vacation_start_date date NOT NULL,
    reason_id integer,
    studentdegree_id integer
);


ALTER TABLE student_academic_vacation OWNER TO postgres;

--
-- Name: student_academic_vacation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_academic_vacation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE student_academic_vacation_id_seq OWNER TO postgres;

--
-- Name: student_academic_vacation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE student_academic_vacation_id_seq OWNED BY student_academic_vacation.id;


--
-- Name: student_degree; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE student_degree (
    id integer NOT NULL,
    active boolean NOT NULL,
    admission_order_date date,
    admission_order_number character varying(15),
    contract_date date,
    contract_number character varying(15),
    diploma_date date,
    diploma_number character varying(15),
    payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    previous_diploma_date date,
    previous_diploma_number character varying(15),
    previous_diploma_type character varying(30) DEFAULT 'SECONDARY_SCHOOL_CERTIFICATE'::character varying NOT NULL,
    protocol_date date,
    protocol_number character varying(10),
    record_book_number character varying(15),
    supplement_date date,
    supplement_number character varying(15),
    thesis_name character varying(150),
    thesis_name_eng character varying(150),
    degree_id integer,
    student_id integer,
    studentgroup_id integer
);


ALTER TABLE student_degree OWNER TO postgres;

--
-- Name: student_degree_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_degree_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE student_degree_id_seq OWNER TO postgres;

--
-- Name: student_degree_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE student_degree_id_seq OWNED BY student_degree.id;


--
-- Name: student_expel; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE student_expel (
    id integer NOT NULL,
    application_date date NOT NULL,
    expel_date date NOT NULL,
    order_date date NOT NULL,
    order_number character varying(15) NOT NULL,
    reason_id integer,
    studentdegree_id integer
);


ALTER TABLE student_expel OWNER TO postgres;

--
-- Name: student_expel_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_expel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE student_expel_id_seq OWNER TO postgres;

--
-- Name: student_expel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE student_expel_id_seq OWNED BY student_expel.id;


--
-- Name: student_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE student_group (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    active boolean NOT NULL,
    begin_years integer NOT NULL,
    creation_year integer NOT NULL,
    study_semesters integer NOT NULL,
    study_years numeric(19,2) NOT NULL,
    tuition_form character varying(10) DEFAULT 'FULL_TIME'::character varying NOT NULL,
    tuition_term character varying(10) DEFAULT 'REGULAR'::character varying NOT NULL,
    specialization_id integer
);


ALTER TABLE student_group OWNER TO postgres;

--
-- Name: student_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE student_group_id_seq OWNER TO postgres;

--
-- Name: student_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE student_group_id_seq OWNED BY student_group.id;


--
-- Name: student_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE student_id_seq OWNER TO postgres;

--
-- Name: student_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE student_id_seq OWNED BY student.id;


--
-- Name: teacher; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE teacher (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    patronimic character varying(20) NOT NULL,
    sex character varying(6) DEFAULT 'MALE'::character varying NOT NULL,
    surname character varying(20) NOT NULL,
    active boolean NOT NULL,
    scientific_degree character varying(255),
    department_id integer,
    position_id integer
);


ALTER TABLE teacher OWNER TO postgres;

--
-- Name: teacher_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE teacher_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE teacher_id_seq OWNER TO postgres;

--
-- Name: teacher_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE teacher_id_seq OWNED BY teacher.id;


--
-- Name: course id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course ALTER COLUMN id SET DEFAULT nextval('course_id_seq'::regclass);


--
-- Name: course_name id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course_name ALTER COLUMN id SET DEFAULT nextval('course_name_id_seq'::regclass);


--
-- Name: courses_for_groups id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups ALTER COLUMN id SET DEFAULT nextval('courses_for_groups_id_seq'::regclass);


--
-- Name: current_year id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY current_year ALTER COLUMN id SET DEFAULT nextval('current_year_id_seq'::regclass);


--
-- Name: degree id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY degree ALTER COLUMN id SET DEFAULT nextval('degree_id_seq'::regclass);


--
-- Name: department id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY department ALTER COLUMN id SET DEFAULT nextval('department_id_seq'::regclass);


--
-- Name: faculty id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY faculty ALTER COLUMN id SET DEFAULT nextval('faculty_id_seq'::regclass);


--
-- Name: grade id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY grade ALTER COLUMN id SET DEFAULT nextval('grade_id_seq'::regclass);


--
-- Name: knowledge_control id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY knowledge_control ALTER COLUMN id SET DEFAULT nextval('knowledge_control_id_seq'::regclass);


--
-- Name: order_reason id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_reason ALTER COLUMN id SET DEFAULT nextval('order_reason_id_seq'::regclass);


--
-- Name: position id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "position" ALTER COLUMN id SET DEFAULT nextval('position_id_seq'::regclass);


--
-- Name: privilege id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY privilege ALTER COLUMN id SET DEFAULT nextval('privilege_id_seq'::regclass);


--
-- Name: speciality id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY speciality ALTER COLUMN id SET DEFAULT nextval('speciality_id_seq'::regclass);


--
-- Name: specialization id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY specialization ALTER COLUMN id SET DEFAULT nextval('specialization_id_seq'::regclass);


--
-- Name: student id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student ALTER COLUMN id SET DEFAULT nextval('student_id_seq'::regclass);


--
-- Name: student_academic_vacation id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_academic_vacation ALTER COLUMN id SET DEFAULT nextval('student_academic_vacation_id_seq'::regclass);


--
-- Name: student_degree id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree ALTER COLUMN id SET DEFAULT nextval('student_degree_id_seq'::regclass);


--
-- Name: student_expel id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_expel ALTER COLUMN id SET DEFAULT nextval('student_expel_id_seq'::regclass);


--
-- Name: student_group id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_group ALTER COLUMN id SET DEFAULT nextval('student_group_id_seq'::regclass);


--
-- Name: teacher id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY teacher ALTER COLUMN id SET DEFAULT nextval('teacher_id_seq'::regclass);


--
-- Name: course_name course_name_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course_name
    ADD CONSTRAINT course_name_pkey PRIMARY KEY (id);


--
-- Name: course course_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course
    ADD CONSTRAINT course_pkey PRIMARY KEY (id);


--
-- Name: courses_for_groups courses_for_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT courses_for_groups_pkey PRIMARY KEY (id);


--
-- Name: current_year current_year_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY current_year
    ADD CONSTRAINT current_year_pkey PRIMARY KEY (id);


--
-- Name: degree degree_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY degree
    ADD CONSTRAINT degree_pkey PRIMARY KEY (id);


--
-- Name: department department_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY department
    ADD CONSTRAINT department_pkey PRIMARY KEY (id);


--
-- Name: faculty faculty_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY faculty
    ADD CONSTRAINT faculty_pkey PRIMARY KEY (id);


--
-- Name: grade grade_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY grade
    ADD CONSTRAINT grade_pkey PRIMARY KEY (id);


--
-- Name: knowledge_control knowledge_control_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY knowledge_control
    ADD CONSTRAINT knowledge_control_pkey PRIMARY KEY (id);


--
-- Name: order_reason order_reason_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_reason
    ADD CONSTRAINT order_reason_pkey PRIMARY KEY (id);


--
-- Name: position position_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "position"
    ADD CONSTRAINT position_pkey PRIMARY KEY (id);


--
-- Name: privilege privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT privilege_pkey PRIMARY KEY (id);


--
-- Name: schema_version schema_version_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schema_version
    ADD CONSTRAINT schema_version_pk PRIMARY KEY (installed_rank);


--
-- Name: speciality speciality_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY speciality
    ADD CONSTRAINT speciality_pkey PRIMARY KEY (id);


--
-- Name: specialization specialization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY specialization
    ADD CONSTRAINT specialization_pkey PRIMARY KEY (id);


--
-- Name: student_academic_vacation student_academic_vacation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_academic_vacation
    ADD CONSTRAINT student_academic_vacation_pkey PRIMARY KEY (id);


--
-- Name: student_degree student_degree_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree
    ADD CONSTRAINT student_degree_pkey PRIMARY KEY (id);


--
-- Name: student_expel student_expel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_expel
    ADD CONSTRAINT student_expel_pkey PRIMARY KEY (id);


--
-- Name: student_group student_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_group
    ADD CONSTRAINT student_group_pkey PRIMARY KEY (id);


--
-- Name: student student_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student
    ADD CONSTRAINT student_pkey PRIMARY KEY (id);


--
-- Name: teacher teacher_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (id);


--
-- Name: faculty uk_757mlj4kyjn7mpb0fb67owj00; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY faculty
    ADD CONSTRAINT uk_757mlj4kyjn7mpb0fb67owj00 UNIQUE (abbr);


--
-- Name: speciality uk_f7cgfesjcj3ygekov90nlvnaa; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY speciality
    ADD CONSTRAINT uk_f7cgfesjcj3ygekov90nlvnaa UNIQUE (code);


--
-- Name: courses_for_groups ukacyne4h2elppn968u5prjg2rd; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT ukacyne4h2elppn968u5prjg2rd UNIQUE (course_id, studentgroup_id);


--
-- Name: degree ukhsy90ut35sobbhgmb5sb5eh7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY degree
    ADD CONSTRAINT ukhsy90ut35sobbhgmb5sb5eh7 UNIQUE (id, name);


--
-- Name: grade ukm4rb6f2efyvns11hpghb8qilv; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY grade
    ADD CONSTRAINT ukm4rb6f2efyvns11hpghb8qilv UNIQUE (course_id, student_id);


--
-- Name: schema_version_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX schema_version_s_idx ON schema_version USING btree (success);


--
-- Name: student_expel fk12i9855ocnh9jj457rloqfeql; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_expel
    ADD CONSTRAINT fk12i9855ocnh9jj457rloqfeql FOREIGN KEY (reason_id) REFERENCES order_reason(id);


--
-- Name: courses_for_groups fk1t8vjy9y1j7cf7s5dorx1t1it; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT fk1t8vjy9y1j7cf7s5dorx1t1it FOREIGN KEY (teacher_id) REFERENCES teacher(id);


--
-- Name: course fk2mi63y0iy38x5tkd3ms6s9qsa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course
    ADD CONSTRAINT fk2mi63y0iy38x5tkd3ms6s9qsa FOREIGN KEY (kc_id) REFERENCES knowledge_control(id);


--
-- Name: teacher fk3ebf13bw5jxpbch977xtuxrax; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY teacher
    ADD CONSTRAINT fk3ebf13bw5jxpbch977xtuxrax FOREIGN KEY (position_id) REFERENCES "position"(id);


--
-- Name: department fk41ox44lg0tyunaxq4y8oyuq8m; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY department
    ADD CONSTRAINT fk41ox44lg0tyunaxq4y8oyuq8m FOREIGN KEY (faculty_id) REFERENCES faculty(id);


--
-- Name: grade fk5secqnjjwgh9wxk4h1xwgj1n0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY grade
    ADD CONSTRAINT fk5secqnjjwgh9wxk4h1xwgj1n0 FOREIGN KEY (student_id) REFERENCES student(id);


--
-- Name: student_degree fk9st6a1j5cw6s3xkakvnavyi99; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree
    ADD CONSTRAINT fk9st6a1j5cw6s3xkakvnavyi99 FOREIGN KEY (degree_id) REFERENCES degree(id);


--
-- Name: student_expel fkbiwtr2vt2n105a3vkk7c18b14; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_expel
    ADD CONSTRAINT fkbiwtr2vt2n105a3vkk7c18b14 FOREIGN KEY (studentdegree_id) REFERENCES student_degree(id);


--
-- Name: course fkfjdhbpuryee3coaafnp1w0y3n; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course
    ADD CONSTRAINT fkfjdhbpuryee3coaafnp1w0y3n FOREIGN KEY (coursename_id) REFERENCES course_name(id);


--
-- Name: courses_for_groups fkfqxp678s005clfsc3ocusfqme; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT fkfqxp678s005clfsc3ocusfqme FOREIGN KEY (course_id) REFERENCES course(id);


--
-- Name: student_academic_vacation fkgc4mk336psudyg6uf3v717u7i; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_academic_vacation
    ADD CONSTRAINT fkgc4mk336psudyg6uf3v717u7i FOREIGN KEY (studentdegree_id) REFERENCES student_degree(id);


--
-- Name: specialization fkjbku080fy3i9fbr82lccuit2h; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY specialization
    ADD CONSTRAINT fkjbku080fy3i9fbr82lccuit2h FOREIGN KEY (speciality_id) REFERENCES speciality(id);


--
-- Name: teacher fkjdcmjbru2mdniqr8rk6phwx6e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY teacher
    ADD CONSTRAINT fkjdcmjbru2mdniqr8rk6phwx6e FOREIGN KEY (department_id) REFERENCES department(id);


--
-- Name: specialization fkjk07jlkbyg2dnhqlhd6udqyld; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY specialization
    ADD CONSTRAINT fkjk07jlkbyg2dnhqlhd6udqyld FOREIGN KEY (faculty_id) REFERENCES faculty(id);


--
-- Name: specialization fkjnv6iiajjgw8x2e55shgj5y8h; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY specialization
    ADD CONSTRAINT fkjnv6iiajjgw8x2e55shgj5y8h FOREIGN KEY (degree_id) REFERENCES degree(id);


--
-- Name: student_degree fkkx4j9d1d7ldnmdyaqtbkokt69; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree
    ADD CONSTRAINT fkkx4j9d1d7ldnmdyaqtbkokt69 FOREIGN KEY (studentgroup_id) REFERENCES student_group(id);


--
-- Name: student_degree fkl2otuowdej7vkvfxbi2tq5jgd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree
    ADD CONSTRAINT fkl2otuowdej7vkvfxbi2tq5jgd FOREIGN KEY (student_id) REFERENCES student(id);


--
-- Name: student_academic_vacation fklaj0en91yngimnh4mnesmnmyq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_academic_vacation
    ADD CONSTRAINT fklaj0en91yngimnh4mnesmnmyq FOREIGN KEY (reason_id) REFERENCES order_reason(id);


--
-- Name: student fkndn9m434jroav4tpm0tvo0vkk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student
    ADD CONSTRAINT fkndn9m434jroav4tpm0tvo0vkk FOREIGN KEY (privilege_id) REFERENCES privilege(id);


--
-- Name: grade fko9djxg4taceuo4s4gyi63kmpf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY grade
    ADD CONSTRAINT fko9djxg4taceuo4s4gyi63kmpf FOREIGN KEY (course_id) REFERENCES course(id);


--
-- Name: courses_for_groups fkoqo9ltw1cci69cr26i7oo6tx4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT fkoqo9ltw1cci69cr26i7oo6tx4 FOREIGN KEY (studentgroup_id) REFERENCES student_group(id);


--
-- Name: specialization fkq2c76cyld8gltdkxod8fosvub; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY specialization
    ADD CONSTRAINT fkq2c76cyld8gltdkxod8fosvub FOREIGN KEY (department_id) REFERENCES department(id);


--
-- Name: student_group fkro5caukwd9ufot4qqkdqcbhx6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_group
    ADD CONSTRAINT fkro5caukwd9ufot4qqkdqcbhx6 FOREIGN KEY (specialization_id) REFERENCES specialization(id);


--
-- PostgreSQL database dump complete
--


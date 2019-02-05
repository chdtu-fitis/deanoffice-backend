--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

--
-- Name: course; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE course (
    id integer NOT NULL,
    credits numeric(4,1) NOT NULL,
    hours integer NOT NULL,
    hours_per_credit integer NOT NULL,
    semester integer NOT NULL,
    course_name_id integer,
    kc_id integer
);


--
-- Name: course_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE course_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    name_eng character varying(100),
    abbreviation character varying(15)
);


--
-- Name: course_name_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE course_name_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: course_name_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE course_name_id_seq OWNED BY course_name.id;


--
-- Name: courses_for_groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE courses_for_groups (
    id integer NOT NULL,
    exam_date date,
    course_id integer NOT NULL,
    student_group_id integer NOT NULL,
    teacher_id integer
);


--
-- Name: courses_for_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE courses_for_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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


--
-- Name: current_year_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE current_year_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    name_eng character varying(100),
    admission_requirements character varying(255),
    admission_requirements_eng character varying(255),
    further_study_access character varying(255),
    further_study_access_eng character varying(255),
    professional_status character varying(255),
    professional_status_eng character varying(255),
    qualification_level_descr character varying(255),
    qualification_level_descr_eng character varying(255)
);


--
-- Name: degree_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE degree_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    faculty_id integer NOT NULL
);


--
-- Name: department_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE department_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    name_eng character varying(100),
    active boolean NOT NULL,
    abbr character varying(20) NOT NULL,
    dean character varying(70)
);


--
-- Name: faculty_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE faculty_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    grade integer,
    points integer,
    course_id integer NOT NULL,
    student_degree_id integer NOT NULL
);


--
-- Name: grade_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE grade_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    name_eng character varying(100),
    graded boolean NOT NULL
);


--
-- Name: knowledge_control_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE knowledge_control_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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


--
-- Name: order_reason_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE order_reason_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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


--
-- Name: position_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE position_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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


--
-- Name: privilege_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE privilege_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: privilege_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE privilege_id_seq OWNED BY privilege.id;


--
-- Name: renewed_academic_vacation_student; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE renewed_academic_vacation_student (
    id integer NOT NULL,
    application_date date NOT NULL,
    payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    renew_date date,
    study_year integer NOT NULL,
    student_academic_vacation_id integer NOT NULL,
    student_group_id integer NOT NULL
);


--
-- Name: renewed_academic_vacation_student_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE renewed_academic_vacation_student_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: renewed_academic_vacation_student_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE renewed_academic_vacation_student_id_seq OWNED BY renewed_academic_vacation_student.id;


--
-- Name: renewed_expelled_student; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE renewed_expelled_student (
    id integer NOT NULL,
    academic_certificate_date date NOT NULL,
    academic_certificate_issued_by character varying(255) NOT NULL,
    academic_certificate_number character varying(255) NOT NULL,
    application_date date NOT NULL,
    payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    renew_date date,
    study_year integer NOT NULL,
    student_expel_id integer NOT NULL,
    student_group_id integer NOT NULL
);


--
-- Name: renewed_expelled_student_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE renewed_expelled_student_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: renewed_expelled_student_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE renewed_expelled_student_id_seq OWNED BY renewed_expelled_student.id;


--
-- Name: speciality; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE speciality (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    name_eng character varying(100),
    active boolean NOT NULL,
    code character varying(20) NOT NULL,
    field_of_study character varying(150),
    field_of_study_eng character varying(150)
);


--
-- Name: speciality_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE speciality_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    name_eng character varying(100),
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
    degree_id integer NOT NULL,
    department_id integer,
    faculty_id integer NOT NULL,
    speciality_id integer NOT NULL
);


--
-- Name: specialization_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE specialization_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    patronimic character varying(20),
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
    photo bytea,
    registration_address character varying(100),
    school character varying(100),
    surname_eng character varying(20),
    telephone character varying(30),
    privilege_id integer
);


--
-- Name: student_academic_vacation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE student_academic_vacation (
    id integer NOT NULL,
    application_date date NOT NULL,
    extra_information character varying(255),
    order_date date NOT NULL,
    order_number character varying(15) NOT NULL,
    study_year integer NOT NULL,
    vacation_end_date date NOT NULL,
    vacation_start_date date NOT NULL,
    order_reason_id integer NOT NULL,
    student_degree_id integer NOT NULL,
    student_group_id integer NOT NULL
);


--
-- Name: student_academic_vacation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_academic_vacation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

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
    student_card_number character varying(15),
    supplement_date date,
    supplement_number character varying(15),
    thesis_name character varying(150),
    thesis_name_eng character varying(150),
    degree_id integer NOT NULL,
    specialization_id integer NOT NULL,
    student_id integer NOT NULL,
    student_group_id integer
);


--
-- Name: student_degree_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_degree_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    payment character varying(8) DEFAULT 'BUDGET'::character varying NOT NULL,
    study_year integer NOT NULL,
    order_reason_id integer NOT NULL,
    student_degree_id integer NOT NULL,
    student_group_id integer NOT NULL
);


--
-- Name: student_expel_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_expel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    specialization_id integer NOT NULL
);


--
-- Name: student_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE student_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
    patronimic character varying(20),
    sex character varying(6) DEFAULT 'MALE'::character varying NOT NULL,
    surname character varying(20) NOT NULL,
    active boolean NOT NULL,
    scientific_degree character varying(255),
    department_id integer NOT NULL,
    position_id integer NOT NULL
);


--
-- Name: teacher_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE teacher_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


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
-- Name: renewed_academic_vacation_student id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renewed_academic_vacation_student ALTER COLUMN id SET DEFAULT nextval('renewed_academic_vacation_student_id_seq'::regclass);


--
-- Name: renewed_expelled_student id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renewed_expelled_student ALTER COLUMN id SET DEFAULT nextval('renewed_expelled_student_id_seq'::regclass);


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
-- Name: renewed_academic_vacation_student renewed_academic_vacation_student_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renewed_academic_vacation_student
    ADD CONSTRAINT renewed_academic_vacation_student_pkey PRIMARY KEY (id);


--
-- Name: renewed_expelled_student renewed_expelled_student_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renewed_expelled_student
    ADD CONSTRAINT renewed_expelled_student_pkey PRIMARY KEY (id);


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
-- Name: courses_for_groups uk19sieop4l7esqbqmc6ictdvph; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT uk19sieop4l7esqbqmc6ictdvph UNIQUE (course_id, student_group_id);


--
-- Name: order_reason uk1otnvcettwbi4id43dh8d5sis; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_reason
    ADD CONSTRAINT uk1otnvcettwbi4id43dh8d5sis UNIQUE (name);


--
-- Name: faculty uk7vcysepqmv2k09mdfa18gj8qw; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY faculty
    ADD CONSTRAINT uk7vcysepqmv2k09mdfa18gj8qw UNIQUE (name);


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
-- Name: department ukbiw7tevwc07g3iutlbmkes0cm; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY department
    ADD CONSTRAINT ukbiw7tevwc07g3iutlbmkes0cm UNIQUE (name);


--
-- Name: degree ukby27bbt64p1ria17hy3khpyft; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY degree
    ADD CONSTRAINT ukby27bbt64p1ria17hy3khpyft UNIQUE (name);


--
-- Name: position ukhct1althd622w7vj7630fvoh3; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "position"
    ADD CONSTRAINT ukhct1althd622w7vj7630fvoh3 UNIQUE (name);


--
-- Name: course_name ukmcev2xfgvg6v572t3h91d7gap; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course_name
    ADD CONSTRAINT ukmcev2xfgvg6v572t3h91d7gap UNIQUE (name);


--
-- Name: knowledge_control uksvc0hwsgvfil1om8pvh41ay2c; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY knowledge_control
    ADD CONSTRAINT uksvc0hwsgvfil1om8pvh41ay2c UNIQUE (name);


--
-- Name: grade ukt7cwyqn470e4ll4g11g1dib1p; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY grade
    ADD CONSTRAINT ukt7cwyqn470e4ll4g11g1dib1p UNIQUE (course_id, student_degree_id);


--
-- Name: privilege uktcjtkyhbxekm2p1hsv6ju9bju; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT uktcjtkyhbxekm2p1hsv6ju9bju UNIQUE (name);


--
-- Name: student_academic_vacation fk1aj9uc687ivslkx3tspud1n9y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_academic_vacation
    ADD CONSTRAINT fk1aj9uc687ivslkx3tspud1n9y FOREIGN KEY (order_reason_id) REFERENCES order_reason(id);


--
-- Name: courses_for_groups fk1t8vjy9y1j7cf7s5dorx1t1it; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT fk1t8vjy9y1j7cf7s5dorx1t1it FOREIGN KEY (teacher_id) REFERENCES teacher(id);


--
-- Name: course fk2wuyv2xldcdi3x9srcaicq89g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course
    ADD CONSTRAINT fk2wuyv2xldcdi3x9srcaicq89g FOREIGN KEY (kc_id) REFERENCES knowledge_control(id);


--
-- Name: teacher fk3ebf13bw5jxpbch977xtuxrax; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY teacher
    ADD CONSTRAINT fk3ebf13bw5jxpbch977xtuxrax FOREIGN KEY (position_id) REFERENCES "position"(id);


--
-- Name: courses_for_groups fk3icq8kpwq3vo1hko4sntb0hin; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT fk3icq8kpwq3vo1hko4sntb0hin FOREIGN KEY (course_id) REFERENCES course(id);


--
-- Name: department fk41ox44lg0tyunaxq4y8oyuq8m; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY department
    ADD CONSTRAINT fk41ox44lg0tyunaxq4y8oyuq8m FOREIGN KEY (faculty_id) REFERENCES faculty(id);


--
-- Name: student_expel fk538kl40vl5ec58jmf1txkmity; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_expel
    ADD CONSTRAINT fk538kl40vl5ec58jmf1txkmity FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);


--
-- Name: grade fk5s1gpik3vodxr5cyqkyn6mkek; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY grade
    ADD CONSTRAINT fk5s1gpik3vodxr5cyqkyn6mkek FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);


--
-- Name: grade fk7e8ca7hfmrpruicqhocskjlf2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY grade
    ADD CONSTRAINT fk7e8ca7hfmrpruicqhocskjlf2 FOREIGN KEY (course_id) REFERENCES course(id);


--
-- Name: renewed_expelled_student fk8i4ftbqmtllto4rwrehaltsdx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renewed_expelled_student
    ADD CONSTRAINT fk8i4ftbqmtllto4rwrehaltsdx FOREIGN KEY (student_group_id) REFERENCES student_group(id);


--
-- Name: student_degree fk9st6a1j5cw6s3xkakvnavyi99; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree
    ADD CONSTRAINT fk9st6a1j5cw6s3xkakvnavyi99 FOREIGN KEY (degree_id) REFERENCES degree(id);


--
-- Name: course fkcurovqow7p3irfphx9ucpx5lr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY course
    ADD CONSTRAINT fkcurovqow7p3irfphx9ucpx5lr FOREIGN KEY (course_name_id) REFERENCES course_name(id);


--
-- Name: student_academic_vacation fke3qe359u8kbkfaim28vw97758; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_academic_vacation
    ADD CONSTRAINT fke3qe359u8kbkfaim28vw97758 FOREIGN KEY (student_degree_id) REFERENCES student_degree(id);


--
-- Name: student_academic_vacation fke6j5q4pv8onchxlkq2m7282ey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_academic_vacation
    ADD CONSTRAINT fke6j5q4pv8onchxlkq2m7282ey FOREIGN KEY (student_group_id) REFERENCES student_group(id);


--
-- Name: student_expel fkfkdbusap38npuwhdye6pg99hy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_expel
    ADD CONSTRAINT fkfkdbusap38npuwhdye6pg99hy FOREIGN KEY (student_group_id) REFERENCES student_group(id);


--
-- Name: student_degree fkgy7f1su2qykfrx8b3snqlssek; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree
    ADD CONSTRAINT fkgy7f1su2qykfrx8b3snqlssek FOREIGN KEY (student_group_id) REFERENCES student_group(id);


--
-- Name: student_degree fkj1llwoe8g31ishojdh9xv44t8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree
    ADD CONSTRAINT fkj1llwoe8g31ishojdh9xv44t8 FOREIGN KEY (specialization_id) REFERENCES specialization(id);


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
-- Name: student_expel fkjn4uf1ps2my5e9poueo8rvook; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_expel
    ADD CONSTRAINT fkjn4uf1ps2my5e9poueo8rvook FOREIGN KEY (order_reason_id) REFERENCES order_reason(id);


--
-- Name: specialization fkjnv6iiajjgw8x2e55shgj5y8h; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY specialization
    ADD CONSTRAINT fkjnv6iiajjgw8x2e55shgj5y8h FOREIGN KEY (degree_id) REFERENCES degree(id);


--
-- Name: student_degree fkl2otuowdej7vkvfxbi2tq5jgd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_degree
    ADD CONSTRAINT fkl2otuowdej7vkvfxbi2tq5jgd FOREIGN KEY (student_id) REFERENCES student(id);


--
-- Name: student fkndn9m434jroav4tpm0tvo0vkk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student
    ADD CONSTRAINT fkndn9m434jroav4tpm0tvo0vkk FOREIGN KEY (privilege_id) REFERENCES privilege(id);


--
-- Name: renewed_academic_vacation_student fknld8adwhu2yp51sdf3tk8j92f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renewed_academic_vacation_student
    ADD CONSTRAINT fknld8adwhu2yp51sdf3tk8j92f FOREIGN KEY (student_group_id) REFERENCES student_group(id);


--
-- Name: specialization fkq2c76cyld8gltdkxod8fosvub; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY specialization
    ADD CONSTRAINT fkq2c76cyld8gltdkxod8fosvub FOREIGN KEY (department_id) REFERENCES department(id);


--
-- Name: renewed_academic_vacation_student fkq630ubwk4twmf2y6rh0kui1n8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renewed_academic_vacation_student
    ADD CONSTRAINT fkq630ubwk4twmf2y6rh0kui1n8 FOREIGN KEY (student_academic_vacation_id) REFERENCES student_academic_vacation(id);


--
-- Name: student_group fkro5caukwd9ufot4qqkdqcbhx6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY student_group
    ADD CONSTRAINT fkro5caukwd9ufot4qqkdqcbhx6 FOREIGN KEY (specialization_id) REFERENCES specialization(id);


--
-- Name: courses_for_groups fkrwsolvxclmfpwhunt70mrlq7s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY courses_for_groups
    ADD CONSTRAINT fkrwsolvxclmfpwhunt70mrlq7s FOREIGN KEY (student_group_id) REFERENCES student_group(id);


--
-- Name: renewed_expelled_student fksc5pauvj5kxci1w5o4ymvpsa5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY renewed_expelled_student
    ADD CONSTRAINT fksc5pauvj5kxci1w5o4ymvpsa5 FOREIGN KEY (student_expel_id) REFERENCES student_expel(id);


--
-- PostgreSQL database dump complete
--


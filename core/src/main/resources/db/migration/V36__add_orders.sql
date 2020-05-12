-- Види наказів: наказ про відрахування, про переведення тощо
create table order_type
(
    id            SERIAL primary key,
    db_table_name varchar(50) not null,
    text_in_order varchar(100) not null,
    introduced_on date not null,
    active        boolean not null default true
);
ALTER TABLE order_type
    ADD CONSTRAINT uk_order_type_db_table_name_and_introduced_on UNIQUE (db_table_name, introduced_on);

-- Used for cases when an order template changes (and we need to give user an previously generated version)
-- template_name -  версія темплейта ордера(docx) - форма документа може змінюваатись з часом
create table order_template_version
(
    id            SERIAL primary key,
    template_name varchar(40) not null,
    order_type_id int not null,
    introduced_on date not null,
    active        boolean not null default true
);
ALTER TABLE order_template_version
    ADD CONSTRAINT fk_order_template_version_order_type_id FOREIGN KEY (order_type_id) REFERENCES order_type (id);
ALTER TABLE order_template_version
    ADD CONSTRAINT uk_order_template_version_order_type_and_introduced_on UNIQUE (order_type_id, introduced_on);

--legal basis - a paragraph which refers to laws on which the order is based
create table legal_basis
(
    id SERIAL primary key,
    legal_basis_text varchar(600) not null,
    introduced_on    date not null,
    active           boolean not null default true
);
ALTER TABLE legal_basis
    ADD CONSTRAINT uk_legal_basis_text_and_introduced_on UNIQUE (legal_basis_text, introduced_on);

-- People who are listed in the bottom of each order (approvers). Will be used to create a set of approvers
-- (order_approve_template). May be improved with faculty id inclusion (to restrict user from seeing templates of other faculties)
create table order_approver
(
    id SERIAL primary key,
    position varchar(150) not null,
    full_name varchar(50) not null,
    faculty_id integer,
    active boolean not null default true
);
ALTER TABLE order_approver
    ADD CONSTRAINT fk_order_approver_faculty_id FOREIGN KEY (faculty_id) REFERENCES faculty (id);
ALTER TABLE order_approver
    ADD CONSTRAINT uk_order_approver_position_and_full_name UNIQUE (position, full_name);

-- User will choose among templates to paste suitable one. Needs discussion on structure and further sequencing in order.
create table order_approve_template
(
    id SERIAL primary key,
    main_approver_id integer not null,
    university_approver_ids integer[] not null,
    faculty_approver_ids integer[] not null,
    initiator_approver_id integer not null,
    faculty_id integer not null,
    active boolean not null default true
);
ALTER TABLE order_approve_template
    ADD CONSTRAINT fk_order_approve_template_main_approver_id FOREIGN KEY (main_approver_id) REFERENCES order_approver (id);
ALTER TABLE order_approve_template
    ADD CONSTRAINT fk_order_approve_template_initiator_approver_id FOREIGN KEY (initiator_approver_id) REFERENCES order_approver (id);
ALTER TABLE order_approve_template
    ADD CONSTRAINT fk_order_approve_template_faculty_id FOREIGN KEY (faculty_id) REFERENCES faculty (id);
-- ALTER TABLE order_approve_template
--     ADD CONSTRAINT uk_order_approve_template_approvers UNIQUE (approvers);

-- Абзац про те, хто контролюватиме виконання наказу
create table order_control_template
(
    id SERIAL primary key,
    control_text varchar(300),
    faculty_id   integer not null,
    active       boolean not null default true
);
ALTER TABLE order_control_template
    ADD CONSTRAINT fk_order_control_template_faculty_id FOREIGN KEY (faculty_id) REFERENCES faculty (id);
ALTER TABLE order_control_template
    ADD CONSTRAINT uk_order_control_template_control_text UNIQUE (control_text);

-- Main business object,=.
-- active == true -> if order has not been deleted (cannot be in signed status).
-- active == true, signed == false -> order is in draft status.
-- active == true, signed == true -> order's business logic has been applied
-- order_control - зазвичай останній абзац у наказі - контроль за виконанням наказу покласти на...
create table orders
(
    id                        SERIAL primary key,
    order_template_version_id int     not null,
    faculty_id                int     not null,
    order_date                date    not null,
    order_number              varchar(15) not null,
    order_type_id             int     not null,
    order_approve_template_id int     not null,
    order_control_template_id integer,
    comment                   varchar(200),
    active                    boolean not null default true,
    signed                    boolean not null default false
);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_order_template_version_id FOREIGN KEY (order_template_version_id) REFERENCES order_templateq_version (id);
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_faculty_id FOREIGN KEY (faculty_id) REFERENCES faculty (id);
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_order_type_id FOREIGN KEY (order_type_id) REFERENCES order_type (id);
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_approve_template_id FOREIGN KEY (order_approve_template_id) REFERENCES order_approve_template (id);
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_order_control_template_id FOREIGN KEY (order_control_template_id) REFERENCES order_control_template (id);
ALTER TABLE orders
    ADD CONSTRAINT uk_orders_faculty_id_and_order_number_and_order_date UNIQUE (faculty_id, order_number, order_date);

-- Paragraphs in order; can be in sections
ALTER TABLE student_expel ADD COLUMN order_id integer not null;
ALTER TABLE student_expel ADD COLUMN tuition_form varchar(10) not null;
ALTER TABLE student_expel ADD COLUMN tuition_term varchar(10) not null;
ALTER TABLE student_expel ADD COLUMN specialization_id integer not null;
ALTER TABLE student_expel ADD COLUMN speciality_id integer not null;
ALTER TABLE student_expel ADD COLUMN reason_document varchar(150) not null;
ALTER TABLE student_expel ADD COLUMN section varchar(50) not null default '';
ALTER TABLE student_expel ADD COLUMN item_text varchar(1000) not null;

ALTER TABLE student_expel ADD CONSTRAINT fk_student_expel_order_id FOREIGN KEY (order_id) REFERENCES orders (id);
ALTER TABLE student_expel ADD CONSTRAINT fk_student_expel_specialization_id FOREIGN KEY (specialization_id) REFERENCES specialization (id);
ALTER TABLE student_expel ADD CONSTRAINT fk_student_expel_speciality_id FOREIGN KEY (speciality_id) REFERENCES speciality (id);
ALTER TABLE student_expel ADD CONSTRAINT uk_student_expel_order_id_student_degree_id UNIQUE (order_id, student_degree_id);

create table name_cases
(
    id SERIAL primary key,
    nominative    varchar(25),
    genitive      varchar(25),
    dative        varchar(25),
    accusative    varchar(25),
    instrumental  varchar(25),
    prepositional varchar(25),
);

create table surname_cases
(
    id SERIAL primary key,
    male_nominative   varchar(25),
    female_nominative varchar(25),
    male_genitive     varchar(25),
    female_genitive   varchar(25),
    male_dative       varchar(25),
    female_dative     varchar(25),
    male_accusative    varchar(25),
    female_accusative    varchar(25),
    male_instrumental  varchar(25),
    female_instrumental  varchar(25),
    male_prepositional varchar(25),
    female_prepositional varchar(25),
);

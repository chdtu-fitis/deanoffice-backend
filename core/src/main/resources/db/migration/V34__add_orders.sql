-- Used for cases when an order template changes (and we need to give user an previously generated version)
create table order_version
(
    id            SERIAL primary key,
    template_name varchar not null,
    document_type varchar not null,
    active        boolean
);
-- People who are listed in the bottom of each order (approvers). Will be used to create a set of approvers
-- (order_approve_template). May be improved with faculty id inclusion (to restrict user from seeing templates of other faculties)
create table order_single_approver
(
    id       SERIAL primary key,
    position varchar,
    initials varchar
);

-- User will choose among templates to paste suitable one. Needs discussion on structure and further sequencing in order.
create table order_approve_template
(
    id                   SERIAL primary key,
    execution_control_id int,
    head_approver_id     int,
    introduced_by_id     int,
    approver_id          int,
    faculty_id           int,
    active               bit
);

ALTER TABLE order_approve_template
    ADD CONSTRAINT fk_execution_control_id FOREIGN KEY (execution_control_id) REFERENCES order_single_approver (id);
ALTER TABLE order_approve_template
    ADD CONSTRAINT head_approver_id FOREIGN KEY (head_approver_id) REFERENCES order_single_approver (id);
ALTER TABLE order_approve_template
    ADD CONSTRAINT fk_introduced_by_id FOREIGN KEY (introduced_by_id) REFERENCES order_single_approver (id);
ALTER TABLE order_approve_template
    ADD CONSTRAINT fk_approver_id FOREIGN KEY (approver_id) REFERENCES order_single_approver (id);
ALTER TABLE order_approve_template
    ADD CONSTRAINT fk_faculty_id FOREIGN KEY (faculty_id) REFERENCES faculty (id);

-- Main business object,=.
-- active == true -> if order has not been deleted (cannot be in signed status).
-- active == true, signed == false -> order is in draft status.
-- active == true, signed == true -> order's business logic has been applied

create table orders
(
    id                        SERIAL primary key,
    order_version_id          int     not null,
    faculty_id                int     not null,
    order_date                date    not null,
    order_name                varchar not null,
    order_reason              int     not null,
    order_approve_template_id int     not null,
    comment                   varchar,
    active                    boolean,
    signed                    boolean
);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_order_version_id FOREIGN KEY (order_version_id) REFERENCES order_version (id);
ALTER TABLE orders
    ADD CONSTRAINT fk_faculty_id FOREIGN KEY (faculty_id) REFERENCES faculty (id);
ALTER TABLE orders
    ADD CONSTRAINT fk_approver_template_id FOREIGN KEY (order_approve_template_id) REFERENCES order_approve_template (id);
ALTER TABLE orders
    ADD CONSTRAINT fk_order_reason_id FOREIGN KEY (order_reason) REFERENCES order_reason (id);


-- Information inputted by user while order generation. Used both for order all order generations (read&write)
create table order_user_input_set
(
    id         SERIAL primary key,
    order_id   int not null,
    user_input json
);

-- mapping that matches template and approver set
create table order_approve_template_mapping
(
    order_id                  int,
    order_approve_template_id int
);
ALTER TABLE order_approve_template_mapping
    ADD CONSTRAINT fk_order_id FOREIGN KEY (order_id) REFERENCES orders (id);
ALTER TABLE order_approve_template_mapping
    ADD CONSTRAINT fk_order_approve_template_id FOREIGN KEY (order_approve_template_id) REFERENCES order_approve_template (id);

ALTER TABLE order_user_input_set
    ADD CONSTRAINT fk_order_id FOREIGN KEY (order_id) REFERENCES orders (id);

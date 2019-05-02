ALTER TABLE student_transfer 
    ADD CONSTRAINT uk_stud_transfer_degree_specialization UNIQUE (old_specialization_id, student_degree_id);
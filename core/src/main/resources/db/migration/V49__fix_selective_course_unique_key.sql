ALTER TABLE selective_course
    DROP CONSTRAINT uk_selective_course_course_id_study_year_degree_id;
ALTER TABLE selective_course
    ADD CONSTRAINT uk_selective_course_course_study_year_degree_field_of_knowledge UNIQUE(course_id, study_year, degree_id, field_of_knowledge_id);

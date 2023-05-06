-- Deleting degree_id in student_degree

alter table student_degree
  drop constraint fk9st6a1j5cw6s3xkakvnavyi99,
  drop column degree_id
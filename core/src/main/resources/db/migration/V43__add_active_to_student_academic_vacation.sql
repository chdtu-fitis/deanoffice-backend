ALTER TABLE student_academic_vacation ADD COLUMN active BOOLEAN DEFAULT true NOT NULL;

UPDATE student_academic_vacation sav SET active = false
FROM renewed_academic_vacation_student ravs
WHERE ravs.student_academic_vacation_id = sav.id;

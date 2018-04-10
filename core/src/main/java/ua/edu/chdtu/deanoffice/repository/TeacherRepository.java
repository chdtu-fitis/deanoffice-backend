package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

}

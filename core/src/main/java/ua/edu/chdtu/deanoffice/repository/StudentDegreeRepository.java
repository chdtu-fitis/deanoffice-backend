package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

public interface StudentDegreeRepository extends JpaRepository<StudentDegree, Integer> {

    StudentDegree getById(Integer id);
}

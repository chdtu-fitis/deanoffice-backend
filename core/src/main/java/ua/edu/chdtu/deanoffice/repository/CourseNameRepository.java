package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.CourseName;

public interface CourseNameRepository extends JpaRepository<CourseName, Integer> {
    CourseName findByName(String name);

}

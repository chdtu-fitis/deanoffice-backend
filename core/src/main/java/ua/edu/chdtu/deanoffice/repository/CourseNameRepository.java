package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.CourseName;

import java.util.List;

public interface CourseNameRepository extends JpaRepository<CourseName, Integer> {
    CourseName findByName(String name);

    @Query(value = "SELECT * FROM course_name cn " +
            "INNER JOIN course c ON c.course_name_id = cn.id " +
            "INNER JOIN courses_for_groups cfg on cfg.course_id = c.id " +
            "INNER JOIN student_group sg ON sg.id = cfg.student_group_id " +
            "Inner join specialization s on s.id = sg.specialization_id " +
            "WHERE sg.active = true and s.active=true AND s.degree_id = :degree_id " +
            "AND floor(sg.creation_year + sg.study_years - 0.1) = :year " +
            "AND s.faculty_id = :faculty_id " +
            "ORDER BY cn.name", nativeQuery = true)
    List<CourseName> findAllForGraduates(
            @Param("year") int year,
            @Param("faculty_id") int facultyId,
            @Param("degree_id") int degreeId
    );
}

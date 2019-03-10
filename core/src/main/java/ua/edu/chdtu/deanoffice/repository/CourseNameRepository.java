package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import java.util.List;

public interface CourseNameRepository extends JpaRepository<CourseName, Integer> {
    CourseName findByName(String name);

    @Query(value = "select " +
            "cn.name as name, " +
            "sg.name as groupName, " +
            "c.semester as semester " +
            "from course_name cn " +
            "join course c on c.course_name_id = cn.id " +
            "join courses_for_groups cfg on cfg.course_id = c.id " +
            "join student_group sg on sg.id = cfg.student_group_id " +
            "join specialization s on s.id = sg.specialization_id " +
            "where sg.active = true and s.active = true and s.degree_id = :degree_id " +
            "and floor(sg.creation_year + sg.study_years - 0.1) = :year " +
            "and s.faculty_id = :faculty_id " +
            "and (cn.name_eng = '' or cn.name_eng is null) " +
            "order by cn.name", nativeQuery = true)
    List<Object[]> findAllForGraduates(
            @Param("year") int year,
            @Param("faculty_id") int facultyId,
            @Param("degree_id") int degreeId
    );
}

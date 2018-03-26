package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {

    @Query("select studentGroup from StudentGroup as studentGroup " +
            "where studentGroup.active = true and studentGroup.specialization.faculty.id = :facultyId order by studentGroup.name")
    List<StudentGroup> findAllActiveByFaculty(@Param("facultyId") int facultyId);

    @Query("select cfg.studentGroup from CourseForGroup as cfg " +
            "where cfg.course.id = :courseId")
    List<StudentGroup> findAllByCourse(@Param("courseId") int courseId);

    @Query(value = "SELECT * FROM student_group sg " +
            "INNER JOIN specialization s ON s.id=sg.specialization_id " +
            "WHERE sg.active=TRUE AND s.degree_id=:degreeId AND floor(sg.creation_year+sg.study_years-0.1)=:currYear " +
            "ORDER BY sg.tuition_form DESC, sg.name", nativeQuery = true)
    List<StudentGroup> findGraduateByDegree(@Param("degreeId") Integer degreeId, @Param("currYear") Integer currYear);

    @Query(value = "SELECT * FROM student_group sg " +
            "INNER JOIN specialization s ON s.id=sg.specialization_id " +
            "WHERE sg.active=TRUE AND s.degree_id=:degreeId AND :currYear-sg.creation_year+sg.begin_years=:studyYear " +
            "ORDER BY sg.tuition_form DESC, sg.name", nativeQuery = true)
    List<StudentGroup> findGroupsByDegreeAndYear(@Param("degreeId") Integer degreeId, @Param("studyYear") Integer studyYear, @Param("currYear") Integer currYear);
}

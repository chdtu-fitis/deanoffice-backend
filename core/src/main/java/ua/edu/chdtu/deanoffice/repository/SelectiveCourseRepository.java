package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import java.util.List;

public interface SelectiveCourseRepository extends JpaRepository<SelectiveCourse, Integer> {

    @Query("SELECT sc FROM SelectiveCourse AS sc WHERE sc.available = true " +
            "AND sc.studyYear = :studyYear " +
            "AND sc.degree.id = :degreeId " +
            "AND sc.course.semester = :semester")
    List<SelectiveCourse> findAllAvailableByStudyYearAndDegreeAndSemester(
            @Param("studyYear") Integer studyYear,
            @Param("degreeId") int degreeId,
            @Param("semester") int semester
    );

    @Query(value = 
            "SELECT sc FROM selective_course AS sc " +
            "INNER JOIN course AS c ON sc.course_id = c.id " +
            "INNER JOIN selective_courses_student_degrees AS scsd ON scsd.selective_course_id = sc.id " +
            "WHERE scsd.student_degree_id = :studentDegreeId " +
            "AND c.semester = :semester", nativeQuery = true)
    List<SelectiveCourse> findAllByStudentDegreeIdAndSemester(
            @Param("studentDegreeId") Integer studentDegreeId,
            @Param("semester") Integer semester
    );
}

package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import java.util.List;

public interface SelectiveCoursesStudentDegreesRepository extends JpaRepository<SelectiveCoursesStudentDegrees, Integer> {
    @Query("SELECT sc FROM SelectiveCoursesStudentDegrees sc " +
            "WHERE sc.selectiveCourse.available = true " +
            "AND sc.selectiveCourse.studyYear = :studyYear " +
            "AND sc.studentDegree.id = :studentDegreeId ")
    List<SelectiveCoursesStudentDegrees> findAllAvailableByStudyYearAndStudentDegree(
            @Param("studyYear") int studyYear,
            @Param("studentDegreeId") int studentDegreeId
    );

    @Query("SELECT sc FROM SelectiveCoursesStudentDegrees sc " +
            "WHERE sc.selectiveCourse.available = true " +
            "AND sc.selectiveCourse.id = :selectiveCourseId ")
    List<SelectiveCoursesStudentDegrees> findAllAvailableByStudyYearAndSelectiveCourse(
            @Param("selectiveCourseId") int selectiveCourseId
    );

    @Query("SELECT scsd FROM SelectiveCoursesStudentDegrees scsd " +
            "WHERE scsd.studentDegree.id = :studentDegreeId")
    List<SelectiveCoursesStudentDegrees> findAllByStudentDegreeId(
            @Param("studentDegreeId") int studentDegreeId
    );
}

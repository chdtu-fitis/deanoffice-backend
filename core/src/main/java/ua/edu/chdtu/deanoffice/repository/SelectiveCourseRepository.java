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
}

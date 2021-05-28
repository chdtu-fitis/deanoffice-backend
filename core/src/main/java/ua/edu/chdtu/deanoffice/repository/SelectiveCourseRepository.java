package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import java.util.List;

public interface SelectiveCourseRepository extends JpaRepository<SelectiveCourse, Integer> {

    @Query("SELECT sc FROM SelectiveCourse AS sc WHERE sc.available = true " +
            "AND sc.studyYear = :studyYear " +
            "AND sc.degree.id = :degreeId " +
            "AND sc.course.semester = :semester " +
            "ORDER BY sc.course.courseName.name")
    List<SelectiveCourse> findAllAvailableByStudyYearAndDegreeAndSemester(
            @Param("studyYear") Integer studyYear,
            @Param("degreeId") int degreeId,
            @Param("semester") int semester
    );

    List<SelectiveCourse> findByIdIn(List<Integer> ids);

    @Modifying
    @Query("UPDATE SelectiveCourse sc SET sc.available = false " +
            "WHERE sc.id IN :selectiveCourseIds")
    void setSelectiveCoursesUnavailableByIds(@Param("selectiveCourseIds") List<Integer> selectiveCourseIds);

    @Query("SELECT sc FROM SelectiveCourse AS sc WHERE sc.available = true " +
            "AND sc.studyYear = :studyYear " +
            "AND sc.degree.id = :degreeId " +
            "AND sc.course.semester IN (:semesters)" +
            "AND sc.id IN (:selectiveCourseIds)")
    List<SelectiveCourse> findAllAvailableByStudyYearAndDegreeAndSemestersAndIds(
            @Param("studyYear") int studyYear,
            @Param("degreeId") int degreeId,
            @Param("semesters") List<Integer> semesters,
            @Param("selectiveCourseIds") List<Integer> selectiveCourseIds
    );
}

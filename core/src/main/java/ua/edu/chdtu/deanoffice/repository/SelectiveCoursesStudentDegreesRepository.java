package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;

import java.util.List;

public interface SelectiveCoursesStudentDegreesRepository extends JpaRepository<SelectiveCoursesStudentDegrees, Integer>, JpaSpecificationExecutor<SelectiveCoursesStudentDegrees> {
    @Override
    List<SelectiveCoursesStudentDegrees> findAll(Specification<SelectiveCoursesStudentDegrees> spec);

    @Query("SELECT sc FROM SelectiveCoursesStudentDegrees sc " +
            "WHERE sc.selectiveCourse.id = :selectiveCourseId " +
            "ORDER BY sc.studentDegree.student.surname, sc.studentDegree.student.name, sc.studentDegree.student.patronimic")
    List<SelectiveCoursesStudentDegrees> findBySelectiveCourse(
            @Param("selectiveCourseId") int selectiveCourseId
    );

    @Query("SELECT sc FROM SelectiveCoursesStudentDegrees sc " +
            "WHERE sc.selectiveCourse.id = :selectiveCourseId AND sc.studentDegree.specialization.faculty.id = :facultyId " +
            "ORDER BY sc.studentDegree.student.surname, sc.studentDegree.student.name, sc.studentDegree.student.patronimic")
    List<SelectiveCoursesStudentDegrees> findBySelectiveCourseAndFaculty(
            @Param("selectiveCourseId") int selectiveCourseId, @Param("facultyId") int facultyId
    );

    @Query("SELECT scsd FROM SelectiveCoursesStudentDegrees scsd " +
            "WHERE scsd.studentDegree.id = :studentDegreeId " +
            "AND scsd.selectiveCourse.course.semester = :semester")
    List<SelectiveCoursesStudentDegrees> findByStudentDegreeAndSemester(
            @Param("studentDegreeId") int studentDegreeId,
            @Param("semester") int semester
    );

    @Query("SELECT scsd FROM SelectiveCoursesStudentDegrees scsd " +
            "WHERE scsd.selectiveCourse.studyYear = :studyYear " +
            "AND scsd.selectiveCourse.course.semester = :semester " +
            "AND scsd.selectiveCourse.degree.id = :degreeId " +
            "AND scsd.active = true")
    List<SelectiveCoursesStudentDegrees> findActiveByYearAndSemesterAndDegree(
            @Param("studyYear") int studyYear,
            @Param("semester") int semester,
            @Param("degreeId") int degreeId
    );

    @Modifying
    @Query("UPDATE SelectiveCoursesStudentDegrees scsd SET scsd.active = false " +
            "WHERE scsd.selectiveCourse.id IN :selectiveCourseIds")
    void setSelectiveCoursesStudentDegreesInactiveBySelectiveCourseIds(@Param("selectiveCourseIds") List<Integer> selectiveCourseIds);

    @Modifying
    @Query(value = "UPDATE selective_courses_student_degrees AS scsd SET active = false " +
            "FROM selective_course AS sc, student_degree AS sd " +
            "WHERE sc.id = scsd.selective_course_id AND sd.id = scsd.student_degree_id " +
            "AND sd.id = :studentDegreeId " +
            "AND sc.study_year = :studyYear " +
            "AND sc.id IN (:selectiveCourseIds)", nativeQuery = true)
    void setSelectiveCoursesStudentDegreesInactiveBySelectiveCourseIdsAndStudentDegreeIdAndStudyYear(@Param("studyYear") int studyYear,
                                                                                                     @Param("studentDegreeId") int studentDegreeId,
                                                                                                     @Param("selectiveCourseIds") List<Integer> selectiveCourseIds);

    @Query("SELECT scsd.selectiveCourse FROM SelectiveCoursesStudentDegrees scsd " +
            "WHERE scsd.studentDegree.id = :studentDegreeId " +
            "AND scsd.selectiveCourse.id IN (:selectiveCourseIds)")
    List<SelectiveCourse> findAllSelectiveCoursesByIdsAndStudentDegreeId(@Param("studentDegreeId") int studentDegreeId,
                                                                         @Param("selectiveCourseIds") List<Integer> selectiveCourseIds);
}

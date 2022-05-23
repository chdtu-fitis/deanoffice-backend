package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IStudentsNotRightSelectiveCoursesNumber;

import java.util.List;

public interface SelectiveCoursesStudentDegreesRepository extends JpaRepository<SelectiveCoursesStudentDegrees, Integer>, JpaSpecificationExecutor<SelectiveCoursesStudentDegrees> {
    @Override
    List<SelectiveCoursesStudentDegrees> findAll(Specification<SelectiveCoursesStudentDegrees> spec);

    List<SelectiveCoursesStudentDegrees> findByStudentDegreeId(int studentDegreeId);

    @Query("SELECT sc FROM SelectiveCoursesStudentDegrees sc " +
            "WHERE sc.selectiveCourse.id = :selectiveCourseId AND sc.active=true " +
            "ORDER BY sc.studentDegree.student.surname, sc.studentDegree.student.name, sc.studentDegree.student.patronimic")
    List<SelectiveCoursesStudentDegrees> findActiveBySelectiveCourse(
            @Param("selectiveCourseId") int selectiveCourseId
    );

    @Query("SELECT sc FROM SelectiveCoursesStudentDegrees sc " +
            "WHERE sc.selectiveCourse.id = :selectiveCourseId AND sc.studentDegree.specialization.faculty.id = :facultyId AND sc.active=true " +
            "ORDER BY sc.studentDegree.student.surname, sc.studentDegree.student.name, sc.studentDegree.student.patronimic")
    List<SelectiveCoursesStudentDegrees> findActiveBySelectiveCourseAndFaculty(
            @Param("selectiveCourseId") int selectiveCourseId, @Param("facultyId") int facultyId
    );

    @Query("SELECT scsd FROM SelectiveCoursesStudentDegrees scsd " +
            "WHERE scsd.studentDegree.id = :studentDegreeId AND scsd.active=true " +
            "AND scsd.selectiveCourse.course.semester = :semester")
    List<SelectiveCoursesStudentDegrees> findActiveByStudentDegreeAndSemester(
            @Param("studentDegreeId") int studentDegreeId,
            @Param("semester") int semester
    );

    @Query("SELECT scsd FROM SelectiveCoursesStudentDegrees scsd " +
            "WHERE scsd.studentDegree.id = :studentDegreeId " +
            "AND scsd.selectiveCourse.studyYear = :year and scsd.active = true")
    List<SelectiveCoursesStudentDegrees> findActiveByStudentDegreeAndYear(
            @Param("studentDegreeId") int studentDegreeId,
            @Param("year") int year
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

    @Query("SELECT DISTINCT scsd.selectiveCourse.course.id FROM SelectiveCoursesStudentDegrees scsd " +
            "WHERE scsd.selectiveCourse.course.semester = :semester " +
            "AND scsd.studentDegree.id in :studentDegreeIds " +
            "AND scsd.active = true")
    List<Integer> findActiveByYearAndSemesterAndStudentDegrees(
            @Param("semester") int semester,
            @Param("studentDegreeIds") List<Integer> studentDegreeIds
    );

    @Modifying
    @Query("UPDATE SelectiveCoursesStudentDegrees scsd SET scsd.active = false " +
            "WHERE scsd.selectiveCourse.id IN :selectiveCourseIds")
    void setSelectiveCoursesStudentDegreesInactiveBySelectiveCourseIds(@Param("selectiveCourseIds") List<Integer> selectiveCourseIds);

    @Modifying
    @Query(value = "UPDATE selective_courses_student_degrees AS scsd SET active = :status " +
            "FROM selective_course AS sc, student_degree AS sd " +
            "WHERE sc.id = scsd.selective_course_id AND sd.id = scsd.student_degree_id " +
            "AND sd.id = :studentDegreeId " +
            "AND sc.study_year = :studyYear " +
            "AND sc.id IN (:selectiveCourseIds)", nativeQuery = true)
    void setSelectiveCoursesStudentDegreesStatusBySelectiveCourseIdsAndStudentDegreeIdAndStudyYear(@Param("studyYear") int studyYear,
                                                                                                   @Param("studentDegreeId") int studentDegreeId,
                                                                                                   @Param("selectiveCourseIds") List<Integer> selectiveCourseIds,
                                                                                                   @Param("status") boolean status);

    @Query("SELECT sd FROM StudentDegree sd WHERE sd.specialization.degree.id = :degreeId and sd.active=TRUE " +
            "and sd.id NOT IN " +
            "(SELECT DISTINCT scsd.studentDegree.id FROM SelectiveCoursesStudentDegrees AS scsd WHERE scsd.selectiveCourse.studyYear= :studyYear)" +
            "ORDER BY sd.student.surname,sd.student.name,sd.student.patronimic")
    List<StudentDegree> findStudentsNotSelectedSelectiveCoursesByDegreeAndStudyYear(
            @Param("studyYear") int studyYear,
            @Param("degreeId") int degreeId
    );

    @Query("SELECT scsd.studentDegree.id, scsd.studentDegree.student.surname, scsd.studentDegree.student.name, scsd.studentDegree.specialization.faculty.name, " +
            "scsd.studentDegree.specialization.speciality.code, :studyYear-scsd.studentDegree.studentGroup.creationYear+scsd.studentDegree.studentGroup.realBeginYear, " +
            "scsd.studentDegree.studentGroup.name, COUNT(scsd.id)" +
            "FROM SelectiveCoursesStudentDegrees AS scsd " +
            "GROUP BY 6, scsd.studentDegree.id, scsd.studentDegree.student.surname, scsd.studentDegree.student.name, " +
            "scsd.studentDegree.specialization.faculty.name, scsd.studentDegree.specialization.speciality.code," +
            "scsd.selectiveCourse.studyYear, scsd.studentDegree.studentGroup.name, scsd.studentDegree.specialization.degree.id " +
            "having scsd.selectiveCourse.studyYear=:studyYear AND COUNT(scsd.id) > 5 and scsd.studentDegree.specialization.degree.id = :degreeId " +
            "order by scsd.studentDegree.studentGroup.name")
    List<IStudentsNotRightSelectiveCoursesNumber> findStudentsSelectedSelectiveCoursesOverNorm(
            Specification<SelectiveCoursesStudentDegrees> specification
    );

    @Query("SELECT scsd.studentDegree.id AS studentDegreeId, " +
            "scsd.studentDegree.student.surname AS surname, " +
            "scsd.studentDegree.student.name AS name, " +
            "scsd.studentDegree.specialization.faculty.name AS facultyName, " +
            "scsd.studentDegree.specialization.speciality.code AS specialityCode, " +
            ":studyYear-scsd.studentDegree.studentGroup.creationYear+scsd.studentDegree.studentGroup.realBeginYear AS year, " +
            "scsd.studentDegree.studentGroup.name AS groupName, " +
            "COUNT(scsd.id) AS coursesNumber " +
            "FROM SelectiveCoursesStudentDegrees AS scsd " +
            "GROUP BY scsd.studentDegree.id, scsd.studentDegree.student.surname, scsd.studentDegree.student.name, " +
            "scsd.studentDegree.specialization.faculty.name, scsd.studentDegree.specialization.speciality.code, 6, " +
            "scsd.studentDegree.studentGroup.name " +
            "having COUNT(scsd.id) < 5 " +
            "order by scsd.studentDegree.studentGroup.name")
    List<IStudentsNotRightSelectiveCoursesNumber> findStudentsSelectedSelectiveCoursesLessNorm(
            Specification<SelectiveCoursesStudentDegrees> specification
    );

}
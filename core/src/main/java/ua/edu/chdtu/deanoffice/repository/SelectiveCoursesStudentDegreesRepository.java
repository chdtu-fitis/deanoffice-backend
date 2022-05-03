package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.ICoursesSelectedByStudentsGroup;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IPercentStudentsRegistrationOnCourses;

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

    @Query(value =
            "SELECT (:currentYear) - scsd.studentDegree.studentGroup.creationYear + scsd.studentDegree.studentGroup.realBeginYear AS studyYear, " +
                    "COUNT(DISTINCT scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.selectiveCourse.studyYear, scsd.studentDegree.specialization.degree.id, scsd.studentDegree.active, 1 " +
                    "HAVING scsd.selectiveCourse.studyYear = :studyYear AND scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY studyYear ")
    List<IPercentStudentsRegistrationOnCourses> findStudentsRegisteredSelectiveCourseByYear(@Param("studyYear") int studyYear,
                                                                                            @Param("degreeId") int degreeId,
                                                                                            @Param("currentYear") int currentYear);
    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.specialization.faculty.abbr AS facultyName, " +
                    "COUNT(DISTINCT scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.selectiveCourse.studyYear, scsd.studentDegree.specialization.degree.id, " +
                    "scsd.studentDegree.active, scsd.studentDegree.studentGroup.specialization.faculty.abbr " +
                    "HAVING scsd.selectiveCourse.studyYear = :studyYear AND scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY facultyName")
    List<IPercentStudentsRegistrationOnCourses> findStudentsRegisteredSelectiveCourseByFaculty(@Param("studyYear") int studyYear,
                                                                                               @Param("degreeId") int degreeId);
    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.name AS groupName, " +
                    "COUNT(DISTINCT scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.selectiveCourse.studyYear, scsd.studentDegree.specialization.degree.id, " +
                    "scsd.studentDegree.active, scsd.studentDegree.studentGroup.name " +
                    "HAVING scsd.selectiveCourse.studyYear = :studyYear AND scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY groupName")
    List<IPercentStudentsRegistrationOnCourses> findStudentsRegisteredSelectiveCourseByGroup(@Param("studyYear") int studyYear,
                                                                                             @Param("degreeId") int degreeId);
    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.specialization.faculty.abbr as facultyName, " +
                    "scsd.studentDegree.specialization.name AS specializationName, " +
                    "COUNT(DISTINCT scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.selectiveCourse.studyYear, scsd.studentDegree.specialization.degree.id, " +
                    "scsd.studentDegree.active, scsd.studentDegree.specialization.name, scsd.studentDegree.studentGroup.specialization.faculty.abbr " +
                    "HAVING scsd.selectiveCourse.studyYear = :studyYear AND scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY facultyName, specializationName ")
    List<IPercentStudentsRegistrationOnCourses> findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(@Param("studyYear") int studyYear,
                                                                                                                @Param("degreeId") int degreeId);
    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.specialization.faculty.abbr AS facultyName, " +
                    "(:currentYear) - scsd.studentDegree.studentGroup.creationYear + scsd.studentDegree.studentGroup.realBeginYear AS studyYear, " +
                    "COUNT(DISTINCT scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.selectiveCourse.studyYear, scsd.studentDegree.specialization.degree.id, scsd.studentDegree.active, " +
                    "scsd.studentDegree.studentGroup.specialization.faculty.abbr, 2 " +
                    "HAVING scsd.selectiveCourse.studyYear = :studyYear AND scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY facultyName, studyYear")
    List<IPercentStudentsRegistrationOnCourses> findStudentsRegisteredSelectiveCourseByFacultyAndYear(@Param("studyYear") int studyYear,
                                                                                                      @Param("degreeId") int degreeId,
                                                                                                      @Param("currentYear") int currentYear);
    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.specialization.faculty.abbr AS facultyName, " +
                    "(:currentYear) - scsd.studentDegree.studentGroup.creationYear + scsd.studentDegree.studentGroup.realBeginYear AS studyYear, " +
                    "scsd.studentDegree.specialization.name AS specializationName, " +
                    "COUNT(DISTINCT scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.selectiveCourse.studyYear, scsd.studentDegree.specialization.degree.id, scsd.studentDegree.specialization.name, scsd.studentDegree.active, " +
                    "scsd.studentDegree.studentGroup.specialization.faculty.abbr, 2 " +
                    "HAVING scsd.selectiveCourse.studyYear = :studyYear AND scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY facultyName, studyYear, specializationName")
    List<IPercentStudentsRegistrationOnCourses> findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(@Param("studyYear") int studyYear,
                                                                                                                       @Param("degreeId") int degreeId,
                                                                                                                       @Param("currentYear") int currentYear);

    @Query(value =
            "SELECT sd.studentGroup.specialization.faculty.abbr AS facultyName, " +
                    "COUNT(sd.id) AS count " +
                    "FROM StudentDegree AS sd " +
                    "WHERE sd.studentGroup.specialization.degree.id=:degreeId AND sd.active = true " +
                    "GROUP BY sd.studentGroup.specialization.faculty.abbr " +
                    "ORDER BY sd.studentGroup.specialization.faculty.abbr")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsOnFaculty(@Param("degreeId") int degreeId);

    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.name AS groupName, " +
                    "COUNT(scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "WHERE scsd.studentDegree.studentGroup.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "GROUP BY scsd.studentDegree.studentGroup.name " +
                    "ORDER BY scsd.studentDegree.studentGroup.name")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsOnGroup(@Param("degreeId") int degreeId);

    @Query(value =
            "SELECT (:currentYear) - scsd.studentDegree.studentGroup.creationYear + scsd.studentDegree.studentGroup.realBeginYear AS studyYear, " +
                    "COUNT(scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "WHERE scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "GROUP BY  scsd.studentDegree.specialization.degree.id, scsd.studentDegree.active, 1 " +
                    "ORDER BY studyYear "
    )
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsOnYears(@Param("degreeId") int degreeId,
                                                                         @Param("currentYear") int currentYear);

    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.specialization.faculty.abbr as facultyName, " +
                    "scsd.studentDegree.specialization.name AS specializationName, " +
                    "COUNT(scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.studentDegree.specialization.degree.id, " +
                    "scsd.studentDegree.active, scsd.studentDegree.specialization.name, scsd.studentDegree.studentGroup.specialization.faculty.abbr " +
                    "HAVING  scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY facultyName, specializationName ")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(@Param("degreeId") int degreeId);

    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.specialization.faculty.abbr as facultyName, " +
                    "(:currentYear) - scsd.studentDegree.studentGroup.creationYear + scsd.studentDegree.studentGroup.realBeginYear AS studyYear, " +
                    "COUNT(DISTINCT scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.studentDegree.specialization.degree.id, " +
                    "scsd.studentDegree.active, scsd.studentDegree.studentGroup.specialization.faculty.abbr, 2 " +
                    "HAVING scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY facultyName, studyYear ")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(@Param("degreeId") int degreeId,
                                                                                                          @Param("currentYear") int currentYear);
    @Query(value =
            "SELECT scsd.studentDegree.studentGroup.specialization.faculty.abbr as facultyName, " +
                    "(:currentYear) - scsd.studentDegree.studentGroup.creationYear + scsd.studentDegree.studentGroup.realBeginYear AS studyYear, " +
                    "scsd.studentDegree.specialization.name AS specializationName, " +
                    "COUNT(DISTINCT scsd.studentDegree.id) AS count " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "GROUP BY scsd.studentDegree.specialization.degree.id, scsd.studentDegree.specialization.name, " +
                    "scsd.studentDegree.active, scsd.studentDegree.studentGroup.specialization.faculty.abbr, 2 " +
                    "HAVING scsd.studentDegree.specialization.degree.id=:degreeId AND scsd.studentDegree.active = true " +
                    "ORDER BY facultyName, studyYear, specializationName ")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(@Param("degreeId") int degreeId,
                                                                                                                           @Param("currentYear") int currentYear);

    @Query(value =
            "SELECT  scsd.selectiveCourse.course.semester AS semester, " +
                    "scsd.selectiveCourse.id AS selectiveCourseId, " +
                    "scsd.studentDegree.id AS studentDegreeId, " +
                    "scsd.selectiveCourse.course.courseName.name AS courseName, " +
                    "scsd.selectiveCourse.trainingCycle AS trainingCycle, " +
                    "fk.code AS fieldOfKnowledgeCode, " +
                    "CONCAT(scsd.studentDegree.student.surname, ' ', scsd.studentDegree.student.name) AS studentFullName " +
                    "FROM SelectiveCoursesStudentDegrees AS scsd " +
                    "JOIN scsd.selectiveCourse AS sc " +
                    "LEFT JOIN sc.fieldOfKnowledge AS fk " +
                    "WHERE scsd.studentDegree.studentGroup.id = :groupId AND scsd.selectiveCourse.studyYear = :studyYear ")
    List<ICoursesSelectedByStudentsGroup> findCoursesSelectedByStudentsGroup(@Param("studyYear") int studyYear,
                                                                             @Param("groupId") int groupId);

}

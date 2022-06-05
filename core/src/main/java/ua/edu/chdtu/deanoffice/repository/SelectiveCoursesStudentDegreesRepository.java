package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.ICoursesSelectedByStudentsGroup;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IFindStudentsByFaculty;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IFindStudentsByFacultyAndSpecialization;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IFindStudentsByFacultyAndYear;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IFindStudentsByFacultyAndYearAndSpecialization;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IFindStudentsByGroup;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IFindStudentsByYear;
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

    @Query("SELECT sd FROM StudentDegree sd WHERE sd.specialization.degree.id = :degreeId and sd.active=TRUE " +
            "and sd.id NOT IN " +
            "(SELECT DISTINCT scsd.studentDegree.id FROM SelectiveCoursesStudentDegrees AS scsd WHERE scsd.selectiveCourse.studyYear= :studyYear)" +
            "ORDER BY sd.student.surname,sd.student.name,sd.student.patronimic")
    List<StudentDegree> findStudentsNotSelectedSelectiveCoursesByDegreeAndStudyYear(
            @Param("studyYear") int studyYear,
            @Param("degreeId") int degreeId
    );
//----------------------- YEAR ------
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "(:currentYear-sg.creation_year+sg.real_begin_year) AS studyYear " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears " +
                    "AND sd.id IN (SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) = :selectiveCourseNumber) " +
                    "GROUP BY studyYear", nativeQuery = true)
    List<IFindStudentsByYear> findStudentsRegisteredSelectiveCourseByYear(@Param("studyYear") int studyYear,
                                                                          @Param("degreeId") int degreeId,
                                                                          @Param("currentYear") int currentYear,
                                                                          @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                          @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);

    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "(:currentYear-sg.creation_year+sg.real_begin_year) AS studyYear " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN (SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) < :selectiveCourseNumber) " +
                    "GROUP BY studyYear", nativeQuery = true)
    List<IFindStudentsByYear> findStudentsRegisteredLessSelectiveCourseByYear(@Param("studyYear") int studyYear,
                                                                              @Param("degreeId") int degreeId,
                                                                              @Param("currentYear") int currentYear,
                                                                              @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                              @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT (:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear AS studyYear, " +
                    "COUNT(DISTINCT sd.id) AS totalCount " +
                    "FROM StudentDegree AS sd " +
                    "WHERE sd.specialization.degree.id=:degreeId AND sd.active = true " +
                    "AND (:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear IN :selectiveCoursesChooseYears " +
                    "GROUP BY  sd.specialization.degree.id, 1 " +
                    "ORDER BY studyYear "
    )
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsOnYears(@Param("degreeId") int degreeId,
                                                                         @Param("currentYear") int currentYear,
                                                                         @Param("selectiveCoursesChooseYears") int[] selectiveCoursesChooseYears);
    //----------------------- FACULTY ------
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "f.abbr AS facultyName " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) = :selectiveCourseNumber) " +
                    "GROUP BY facultyName", nativeQuery = true)
    List<IFindStudentsByFaculty> findStudentsRegisteredSelectiveCourseByFaculty(@Param("studyYear") int studyYear,
                                                                                @Param("degreeId") int degreeId,
                                                                                @Param("currentYear") int currentYear,
                                                                                @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "f.abbr AS facultyName " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) < :selectiveCourseNumber) " +
                    "GROUP BY facultyName", nativeQuery = true)
    List<IFindStudentsByFaculty> findStudentsRegisteredLessSelectiveCourseByFaculty(@Param("studyYear") int studyYear,
                                                                                    @Param("degreeId") int degreeId,
                                                                                    @Param("currentYear") int currentYear,
                                                                                    @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                    @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT sd.studentGroup.specialization.faculty.abbr AS facultyName, " +
                    "COUNT(DISTINCT sd.id) AS totalCount " +
                    "FROM StudentDegree AS sd " +
                    "WHERE sd.studentGroup.specialization.degree.id=:degreeId AND sd.active = true " +
                    "AND (:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear IN :selectiveCoursesChooseYears " +
                    "GROUP BY sd.studentGroup.specialization.faculty.abbr " +
                    "ORDER BY sd.studentGroup.specialization.faculty.abbr")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsOnFaculty(@Param("degreeId") int degreeId,
                                                                           @Param("currentYear") int currentYear,
                                                                           @Param("selectiveCoursesChooseYears") int[] selectiveCoursesChooseYears);
    //----------------------- GROUP ------
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "dp.abbr AS department, " +
                    "f.abbr AS facultyName, " +
                    "sg.name AS groupName, " +
                    "(:currentYear-sg.creation_year+sg.real_begin_year) AS studyYear " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "join department dp on dp.id=sz.department_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) = :selectiveCourseNumber) " +
                    "GROUP BY groupName, facultyName, studyYear, department", nativeQuery = true)
    List<IFindStudentsByGroup> findStudentsRegisteredSelectiveCourseByGroup(@Param("studyYear") int studyYear,
                                                                            @Param("degreeId") int degreeId,
                                                                            @Param("currentYear") int currentYear,
                                                                            @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                            @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "dp.abbr AS department, " +
                    "f.abbr AS facultyName, " +
                    "sg.name AS groupName, " +
                    "(:currentYear-sg.creation_year+sg.real_begin_year) AS studyYear " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "join department dp on dp.id=sz.department_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) < :selectiveCourseNumber) " +
                    "GROUP BY groupName, facultyName, studyYear, department", nativeQuery = true)
    List<IFindStudentsByGroup> findStudentsRegisteredLessSelectiveCourseByGroup(@Param("studyYear") int studyYear,
                                                                                @Param("degreeId") int degreeId,
                                                                                @Param("currentYear") int currentYear,
                                                                                @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT sd.studentGroup.name AS groupName," +
                    "sd.studentGroup.specialization.faculty.abbr as facultyName, " +
                    "(:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear AS studyYear, " +
                    "sd.studentGroup.specialization.department.abbr AS department, " +
                    "COUNT(DISTINCT sd.id) AS totalCount " +
                    "FROM StudentDegree AS sd " +
                    "WHERE sd.studentGroup.specialization.degree.id=:degreeId AND sd.active = true " +
                    "AND (:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear IN :selectiveCoursesChooseYears " +
                    "GROUP BY sd.studentGroup.name, sd.studentGroup.specialization.faculty.abbr, 3," +
                    "sd.studentGroup.specialization.department.abbr " +
                    "ORDER BY sd.studentGroup.name")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsOnGroup(@Param("degreeId") int degreeId,
                                                                         @Param("currentYear") int currentYear,
                                                                         @Param("selectiveCoursesChooseYears") int[] selectiveCoursesChooseYears);
//---------------- FACULTY_AND_SPECIALIZATION --------------
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "f.abbr AS facultyName, " +
                    "sz.name AS specializationName  " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) = :selectiveCourseNumber) " +
                    "GROUP BY facultyName, specializationName ", nativeQuery = true)
    List<IFindStudentsByFacultyAndSpecialization> findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(@Param("studyYear") int studyYear,
                                                                                                                  @Param("degreeId") int degreeId,
                                                                                                                  @Param("currentYear") int currentYear,
                                                                                                                  @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                                                  @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "f.abbr AS facultyName, " +
                    "sz.name AS specializationName " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) < :selectiveCourseNumber) " +
                    "GROUP BY facultyName, specializationName ", nativeQuery = true)
    List<IFindStudentsByFacultyAndSpecialization> findStudentsRegisteredLessSelectiveCourseByFacultyAndSpecialization(@Param("studyYear") int studyYear,
                                                                                                                      @Param("degreeId") int degreeId,
                                                                                                                      @Param("currentYear") int currentYear,
                                                                                                                      @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                                                      @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT sd.studentGroup.specialization.faculty.abbr as facultyName, " +
                    "sd.specialization.name AS specializationName, " +
                    "COUNT(DISTINCT sd.id) AS totalCount " +
                    "FROM StudentDegree AS sd " +
                    "WHERE  sd.specialization.degree.id=:degreeId AND sd.active = true " +
                    "AND (:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear IN :selectiveCoursesChooseYears " +
                    "GROUP BY sd.specialization.degree.id, " +
                    "sd.specialization.name, sd.studentGroup.specialization.faculty.abbr " +
                    "ORDER BY facultyName, specializationName ")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(@Param("degreeId") int degreeId,
                                                                                                                    @Param("currentYear") int currentYear,
                                                                                                                    @Param("selectiveCoursesChooseYears") int[] selectiveCoursesChooseYears);

//-------------- FACULTY_AND_YEAR ---------
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "f.abbr AS facultyName, " +
                    "(:currentYear-sg.creation_year+sg.real_begin_year) AS studyYear " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "join department dp on dp.id=sz.department_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) = :selectiveCourseNumber) " +
                    "GROUP BY facultyName, studyYear", nativeQuery = true)
    List<IFindStudentsByFacultyAndYear> findStudentsRegisteredSelectiveCourseByFacultyAndYear(@Param("studyYear") int studyYear,
                                                                                              @Param("degreeId") int degreeId,
                                                                                              @Param("currentYear") int currentYear,
                                                                                              @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                              @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "f.abbr AS facultyName, " +
                    "(:currentYear-sg.creation_year+sg.real_begin_year) AS studyYear " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "join department dp on dp.id=sz.department_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) < :selectiveCourseNumber) " +
                    "GROUP BY facultyName, studyYear", nativeQuery = true)
    List<IFindStudentsByFacultyAndYear> findStudentsRegisteredLessSelectiveCourseByFacultyAndYear(@Param("studyYear") int studyYear,
                                                                                                  @Param("degreeId") int degreeId,
                                                                                                  @Param("currentYear") int currentYear,
                                                                                                  @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                                  @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT sd.studentGroup.specialization.faculty.abbr as facultyName, " +
                    "(:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear AS studyYear, " +
                    "COUNT(DISTINCT sd.id) AS totalCount " +
                    "FROM StudentDegree AS sd " +
                    "WHERE sd.specialization.degree.id=:degreeId AND sd.active = true " +
                    "AND (:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear IN :selectiveCoursesChooseYears " +
                    "GROUP BY sd.specialization.degree.id, " +
                    "sd.studentGroup.specialization.faculty.abbr, 2 " +
                    "ORDER BY facultyName, studyYear ")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(@Param("degreeId") int degreeId,
                                                                                                          @Param("currentYear") int currentYear,
                                                                                                          @Param("selectiveCoursesChooseYears") int[] selectiveCoursesChooseYears);
//--------------  FACULTY_AND_YEAR_AND_SPECIALIZATION  ----------------
@Query(value =
        "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                "f.abbr AS facultyName, " +
                "sz.name AS specializationName, " +
                "(:currentYear-sg.creation_year+sg.real_begin_year) AS studyYear " +
                "FROM selective_courses_student_degrees scsd " +
                "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                "join student_degree sd on sd.id=scsd.student_degree_id " +
                "join student_group sg on sg.id=sd.student_group_id " +
                "join specialization sz on sz.id=sd.specialization_id " +
                "join department dp on dp.id=sz.department_id " +
                "JOIN faculty f ON f.id = sz.faculty_id " +
                "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                "join student_degree sd on sd.id=scsd.student_degree_id " +
                "join specialization sz on sz.id=sd.specialization_id " +
                "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                "GROUP BY sd.id " +
                "HAVING COUNT(scsd.id) = :selectiveCourseNumber) " +
                "GROUP BY facultyName, studyYear, specializationName", nativeQuery = true)
List<IFindStudentsByFacultyAndYearAndSpecialization> findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(@Param("studyYear") int studyYear,
                                                                                                                            @Param("degreeId") int degreeId,
                                                                                                                            @Param("currentYear") int currentYear,
                                                                                                                            @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                                                            @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);
    @Query(value =
            "SELECT COUNT(DISTINCT scsd.student_degree_id) AS registeredCount, " +
                    "f.abbr AS facultyName, " +
                    "sz.name AS specializationName, " +
                    "(:currentYear-sg.creation_year+sg.real_begin_year) AS studyYear " +
                    "FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join student_group sg on sg.id=sd.student_group_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "join department dp on dp.id=sz.department_id " +
                    "JOIN faculty f ON f.id = sz.faculty_id " +
                    "WHERE sd.active=true AND (:currentYear-sg.creation_year+sg.real_begin_year) = :selectiveCoursesChooseYears AND sd.id IN " +
                    "(SELECT sd.id FROM selective_courses_student_degrees scsd " +
                    "JOIN selective_course sc ON sc.id = scsd.selective_course_id " +
                    "join student_degree sd on sd.id=scsd.student_degree_id " +
                    "join specialization sz on sz.id=sd.specialization_id " +
                    "WHERE scsd.active=TRUE AND sc.study_year=:studyYear AND sz.degree_id=:degreeId " +
                    "GROUP BY sd.id " +
                    "HAVING COUNT(scsd.id) < :selectiveCourseNumber) " +
                    "GROUP BY facultyName, studyYear, specializationName", nativeQuery = true)
    List<IFindStudentsByFacultyAndYearAndSpecialization> findStudentsRegisteredLessSelectiveCourseByFacultyAndYearAndSpecialization(@Param("studyYear") int studyYear,
                                                                                                                                    @Param("degreeId") int degreeId,
                                                                                                                                    @Param("currentYear") int currentYear,
                                                                                                                                    @Param("selectiveCourseNumber") int selectiveCourseNumber,
                                                                                                                                    @Param("selectiveCoursesChooseYears") int selectiveCoursesChooseYears);

    @Query(value =
            "SELECT sd.studentGroup.specialization.faculty.abbr as facultyName, " +
                    "(:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear AS studyYear, " +
                    "sd.specialization.name AS specializationName, " +
                    "COUNT(DISTINCT sd.id) AS totalCount " +
                    "FROM StudentDegree AS sd " +
                    "WHERE sd.specialization.degree.id=:degreeId AND sd.active = true " +
                    "AND (:currentYear) - sd.studentGroup.creationYear + sd.studentGroup.realBeginYear IN :selectiveCoursesChooseYears " +
                    "GROUP BY sd.specialization.degree.id, sd.specialization.name, " +
                    "sd.studentGroup.specialization.faculty.abbr, 2 " +
                    "ORDER BY facultyName, studyYear, specializationName ")
    List<IPercentStudentsRegistrationOnCourses> findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(@Param("degreeId") int degreeId,
                                                                                                                           @Param("currentYear") int currentYear,
                                                                                                                           @Param("selectiveCoursesChooseYears") int[] selectiveCoursesChooseYears);


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
                    "WHERE scsd.active=true AND scsd.studentDegree.studentGroup.id=:groupId AND scsd.selectiveCourse.studyYear=:studyYear ")
    List<ICoursesSelectedByStudentsGroup> findCoursesSelectedByStudentsGroup(@Param("studyYear") int studyYear,
                                                                             @Param("groupId") int groupId);

    @Query(value =
            "SELECT  DISTINCT(sd.id) AS studentDegreeId, " +
                    "CONCAT(sd.student.surname, ' ', sd.student.name) AS studentFullName " +
                    "FROM StudentDegree AS sd " +
                    "WHERE sd.active=true AND sd.studentGroup.id=:groupId " +
                    "AND sd.id NOT IN :registeredStudentsIds")
    List<ICoursesSelectedByStudentsGroup> findNotRegisteredStudents(@Param("groupId") int groupId,
                                                                    @Param("registeredStudentsIds") List<Integer> registeredStudentsIds);
}

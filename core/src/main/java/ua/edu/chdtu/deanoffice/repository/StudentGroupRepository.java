package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;

import java.util.List;
import java.util.Set;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {

    @Query("select sg from StudentGroup as sg " +
            "where sg.active = :active and sg.specialization.faculty.id = :facultyId " +
            "order by sg.name")
    List<StudentGroup> findByActiveAndFaculty(@Param("active") boolean active, @Param("facultyId") int facultyId);

    @Query("SELECT sg FROM StudentGroup AS sg " +
            "WHERE sg.active = TRUE " +
            "ORDER BY sg.name")
    List<StudentGroup> findAllActive();

    @Query("select sg from StudentGroup as sg " +
            "where sg.specialization.faculty.id = :facultyId " +
            "order by sg.name")
    List<StudentGroup> findAllByFaculty(@Param("facultyId") int facultyId);

    @Query("select cfg.studentGroup from CourseForGroup as cfg " +
            "where cfg.course.id = :courseId " +
            "and cfg.studentGroup.specialization.faculty.id = :faculty_id")
    List<StudentGroup> findAllByCourse(@Param("courseId") int courseId, @Param("faculty_id") int facultyId);

    @Query(value = "SELECT * FROM student_group sg " +
            "INNER JOIN specialization s ON s.id = sg.specialization_id " +
            "WHERE sg.active = TRUE AND s.degree_id = :degreeId " +
            "AND floor(sg.creation_year + sg.study_years - 0.01) <= :currYear " +
            "AND s.faculty_id = :faculty_id " +
            "ORDER BY sg.tuition_form DESC, sg.name", nativeQuery = true)
    List<StudentGroup> findGraduateByDegree(
            @Param("degreeId") Integer degreeId,
            @Param("currYear") Integer currYear,
            @Param("faculty_id") int facultyId
    );

    @Query("select sg from StudentGroup sg " +
            "where sg.active = true " +
            "and sg.specialization.degree.id = :degree_id " +
            "and :curr_year - sg.creationYear + sg.beginYears = :study_year " +
            "and sg.specialization.faculty.id = :faculty_id " +
            "order by sg.tuitionForm desc, sg.name")
    List<StudentGroup> findGroupsByDegreeAndYear(
            @Param("degree_id") Integer degreeId,
            @Param("study_year") Integer studyYear,
            @Param("curr_year") Integer currYear,
            @Param("faculty_id") int facultyId
    );

    @Query("select sg from StudentGroup sg " +
            "where sg.active = true " +
            "and sg.specialization.degree.id = :degree_id " +
            "and :curr_year - sg.creationYear + sg.beginYears = :study_year " +
            "and sg.specialization.faculty.id = :faculty_id " +
            "and sg.tuitionForm = :tuitionForm " +
            "order by sg.name")
    List<StudentGroup> findGroupsByDegreeAndYearAndTuitionForm(
            @Param("degree_id") Integer degreeId,
            @Param("study_year") Integer studyYear,
            @Param("curr_year") Integer currYear,
            @Param("faculty_id") int facultyId,
            @Param("tuitionForm") TuitionForm tuitionForm
    );

    @Query("select sg from StudentGroup sg " +
            "where sg.id in :group_ids")
    List<StudentGroup> findAllByIds(@Param("group_ids") List<Integer> groupIds);

    @Query(value =
            "SELECT *\n" +
                    "FROM student_group\n" +
                    "       INNER JOIN courses_for_groups cfg ON student_group.id = cfg.student_group_id\n" +
                    "       INNER JOIN specialization sp ON specialization_id = sp.id\n" +
                    "       INNER JOIN course c ON cfg.course_id = c.id\n" +
                    "WHERE cfg.course_id IN (SELECT course.id\n" +
                    "                        FROM course\n" +
                    "                        WHERE course.hours IN (SELECT c2.hours FROM course c2 WHERE c2.id = :course_id)\n" +
                    "                          AND\n" +
                    "                            course.course_name_id IN (SELECT c3.course_name_id FROM course c3 WHERE c3.id = :course_id)\n" +
                    "                          AND course.kc_id IN (SELECT c4.kc_id FROM course c4 WHERE c4.id = :course_id))\n" +
                    "  AND sp.faculty_id = :faculty_id\n" +
                    "  AND sp.degree_id = :degree_id\n" +
                    "  AND c.semester = ((((SELECT curr_year FROM current_year) - student_group.creation_year) * 2 + 1) +\n" +
                    "                    cast((NOT date_part('year', CURRENT_DATE) = (SELECT curr_year FROM current_year)) AS int))", nativeQuery = true)
    List<StudentGroup> findGroupsThatAreStudyingSameCourseTo(
            @Param("course_id") Integer courseId,
            @Param("faculty_id") Integer facultyId,
            @Param("degree_id") Integer degreeId
    );

    @Query("SELECT sg from StudentGroup sg " +
            "where sg.active = true " +
            "and sg.name = :name " +
            "and sg.specialization.faculty.id = :faculty_id")
    List<StudentGroup> findByName(@Param("name") String name, @Param("faculty_id") int facultyId);

    @Modifying
    @Query(value = "UPDATE student_group sg SET active = false WHERE sg.id IN (:ids)", nativeQuery = true)
    void setStudentGroupInactiveByIds(@Param("ids") Set<Integer> ids);

    @Query("select sg from StudentGroup as sg " +
            "where sg.specialization.id = :specialization_id " +
            "and sg.id = :student_group_id " +
            "order by sg.name")
    List<StudentGroup> findAllBySpecializationIdAndGroupId(
            @Param("specialization_id") Integer specializationId,
            @Param("student_group_id") Integer studentGroupId
    );

    @Query("SELECT sg from StudentGroup sg " +
            "WHERE sg.specialization.id = :specializationId " +
            "and sg.active = true")
    List<StudentGroup> findBySpecializationId(@Param("specializationId") int specializationId);

    @Query(value = "SELECT count(sg.id) FROM student_group sg WHERE sg.id IN (:ids) AND sg.active = false", nativeQuery = true)
    int countInactiveStudentGroupsByIds(@Param("ids") List<Integer> ids);

    @Query("select sg from StudentGroup sg " +
            "where active = :active and sg.name in " +
            "(select substring(fg.name,1,length(fg.name) - 2) from StudentGroup fg " +
            "where fg.specialization.faculty.id = 8 and fg.active = :active)"
    )
    List<StudentGroup> findStudentGroupsMatchingForeignGroups(@Param("active") Boolean active);

    List<StudentGroup> findAll(Specification<StudentGroup> specification);
}

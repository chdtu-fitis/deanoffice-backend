package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.Grade;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Integer> {

    @Query("Select grade From Grade grade" +
            " Join grade.course course" +
            " Where grade.studentDegree.id = :studentDegreeId" +
            " and course.knowledgeControl.id in (:KnowledgeControlIds)" +
            " and course.id in (:courseIds)" +
            " Order by course.courseName.name")
    List<Grade> getByStudentDegreeIdAndCoursesAndKCTypes(
            @Param("studentDegreeId") Integer studentDegreeId,
            @Param("courseIds") List<Integer> courseIds,
            @Param("KnowledgeControlIds") List<Integer> knowledgeControlsIds
    );

    @Query(value = "select g.* from grade g" +
            " inner join course c ON c.id = g.course_id" +
            " inner join course_name cn ON cn.id = c.course_name_id" +
            " inner join student_degree sd ON sd.id = g.student_degree_id" +
            " inner join student s ON s.id = sd.student_id" +
            " inner join student_group sg ON sg.id = sd.student_group_id" +
            " inner join courses_for_groups cfg ON cfg.course_id = c.id and sg.id=cfg.student_group_id" +
            " where g.student_degree_id = :studentDegreeId and (g.points is null or g.points < " + Constants.MINIMAL_SATISFACTORY_POINTS + ")" +
            " order by c.semester, cn.name", nativeQuery = true)
    List<Grade> getByCheckStudentGradesForSupplement(
            @Param("studentDegreeId") Integer studentDegreeId
    );

    @Query(value = "select g.* from grade g" +
            " inner join course c ON c.id = g.course_id" +
            " inner join course_name cn ON cn.id = c.course_name_id" +
            " inner join student_degree sd ON sd.id = g.student_degree_id" +
            " inner join selective_courses_student_degrees scsd ON scsd.student_degree_id = g.student_degree_id" +
            " inner join selective_course sc ON sc.course_id = g.course_id AND sc.id = scsd.selective_course_id" +
            " where scsd.active = true and g.student_degree_id = :studentDegreeId and (g.points is null or g.points < " + Constants.MINIMAL_SATISFACTORY_POINTS + ")" +
            " order by c.semester, cn.name", nativeQuery = true)
    List<Grade> getByCheckStudentSelectiveCoursesGradesForSupplement(
            @Param("studentDegreeId") Integer studentDegreeId
    );

    @Query("select grade from Grade grade " +
            "join grade.course course " +
            "where grade.studentDegree.id in (:studentIds)" +
            "and course.id in (:courseIds)")
    List<Grade> findGradesByCourseAndBySemesterForStudents(
            @Param("studentIds") List<Integer> studentIds,
            @Param("courseIds") List<Integer> courseIds);

    Grade getByStudentDegreeIdAndCourseId(Integer studentDegreeId, Integer courseId);

    @Query("select gr from Grade gr where gr.course.id = :courseId and gr.studentDegree.studentGroup.id = :groupId")
    List<Grade> findByCourseAndGroup(@Param("courseId") int courseId, @Param("groupId") int groupId);

    @Query(value =
            "SELECT CAST(CAST(COUNT(*) AS INT) AS BOOLEAN) AS is_good_mark" +
                    "  FROM student_degree" +
                    "        LEFT JOIN grade ON student_degree.id = grade.student_degree_id" +
                    " WHERE student_degree.id = :studentDegreeId " +
                    "       AND student_degree.student_group_id = :studentGroupId " +
                    "       AND grade.points >= 60 " +
                    "       AND course_id IN (SELECT similar_course.id " +
                    "                           FROM course similar_course " +
                    "                          WHERE course_name_id IN (SELECT course_name.course_name_id " +
                    "                                                     FROM course course_name " +
                    "                                                    WHERE course_name.id = :courseId) " +
                    "                                AND kc_id IN (SELECT course_kc.kc_id " +
                    "                                                FROM course course_kc " +
                    "                                               WHERE course_kc.id = :courseId) " +
                    "                                AND hours IN (SELECT course_hours.hours " +
                    "                                                FROM course course_hours " +
                    "                                               WHERE course_hours.id = :courseId)) ",
            nativeQuery = true)
    boolean isStudentHaveGoodMarkFromCourse(@Param("studentDegreeId") Integer studentDegreeId,
                                            @Param("studentGroupId") Integer studentGroupId,
                                            @Param("courseId") Integer courseId);

    @Modifying
    @Query("update Grade as g set g.course.id = :newId " +
            "where g.course.id = :oldId")
    void updateCourseIdByCourseId(@Param("newId") int newId, @Param("oldId") int oldId);

    @Modifying
    @Query(value = "UPDATE Grade g " +
            "SET g.academicDifference = :academicDifference " +
            "WHERE g.id IN :gradeIds")
    void updateAcademicDifference(@Param("academicDifference") boolean academicDifference, @Param("gradeIds") List<Integer> gradeIds);

    @Modifying
    @Query(value = "UPDATE Grade AS gr " +
            "set grade = " +
            "case " +
            "when points < 60 or points = null or points = 0 then 0 " +
            "when points >= 60 and points <= 73 then 3 " +
            "when points >= 74 and points <= 89 then 4 " +
            "else 5 " +
            "END " +
            "from student_degree sd " +
            "inner join student_group sg on sd.student_group_id = sg.id " +
            "where gr.course_id = :courseId and gr.student_degree_id = :studentDegreeId  and  sg.id = sd.student_group_id ", nativeQuery = true)
    void updateGradeByCourseIdAndGradedTrue(@Param("courseId") int courseId, @Param("studentDegreeId") int studentDegreeId);

    @Modifying
    @Query(value = "UPDATE Grade AS gr " +
            "set grade = " +
            "case " +
            "when points >= 60 then 1 " +
            "else 0 " +
            "END " +
            "from student_degree sd " +
            "where gr.course_id = :courseId and gr.student_degree_id = :studentDegreeId", nativeQuery = true)
    void updateGradeByCourseIdAndGradedFalse(@Param("courseId") int courseId, @Param("studentDegreeId") int studentDegreeId);

    @Query(value = "select gr.student_degree_id from Grade as gr " +
            "where gr.course_id = :courseId ", nativeQuery = true)
    List<Integer> getStudentDegreeIdByCourseId(@Param("courseId") int courseId);

    @Modifying
    @Query(value = "UPDATE grade as g " +
            "SET academic_difference = :academicDifference " +
            "from student_degree sd " +
            "where sd.id = g.student_degree_id " +
            "and g.course_id = :courseId " +
            "and sd.student_group_id = :groupId", nativeQuery = true)
    void updateAcademicDifferenceByCourseIdAndGroupId(@Param("academicDifference") boolean academicDifference, @Param("groupId") int groupId, @Param("courseId") int courseId);

    @Query("Select grade From Grade grade" +
            " Join grade.course course" +
            " Where grade.studentDegree.id = :studentDegreeId" +
            " And course.knowledgeControl.id in (:knowledgeControlIds)" +
            " And course.id in (:courseIds)" +
            " Order by course.courseName.name")
    public List<Grade> getByStudentDegreeIdAndCoursesIdsKCTypes(
            @Param("studentDegreeId") Integer studentDegreeId,
            @Param("courseIds") List<Integer> courseIds,
            @Param("knowledgeControlIds") List<Integer> knowledgeControlsIds
    );

    @Query("Select grade From Grade grade" +
            " Join grade.course course" +
            " Where course.id in (:courseIds)" +
            " and  grade.studentDegree.id = :studentDegreeId" +
            " Order by course.courseName.name")
    public List<Grade> getByStudentDegreeIdAndCourses(
            @Param("studentDegreeId") Integer studentDegreeId,
            @Param("courseIds") List<Integer> courseIds
    );

    @Query("select g from Grade g" +
            " where g.studentDegree.id=:studentDegreeId" +
            " and g.course.semester=:semester" +
            " order by course.knowledgeControl.id, course.courseName.name")
    public List<Grade> getByStudentDegreeIdAndSemester(
            @Param("studentDegreeId") Integer studentDegreeId,
            @Param("semester") Integer semester
    );
}

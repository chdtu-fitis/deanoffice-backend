package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Course;
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
            " left outer join courses_for_groups cfg ON cfg.course_id = c.id and sg.id=cfg.student_group_id" +
            " where g.student_degree_id = :studentDegreeId" +
            " order by cn.name", nativeQuery = true)
    List<Grade> getByCheckStudentGradesForSupplement(
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

}

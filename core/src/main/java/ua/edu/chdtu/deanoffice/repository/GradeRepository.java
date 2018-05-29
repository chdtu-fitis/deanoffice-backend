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

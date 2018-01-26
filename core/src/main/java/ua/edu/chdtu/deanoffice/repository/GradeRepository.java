package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Grade;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Integer> {

    @Query("Select g From Grade g" +
            " Join g.course course" +
            " Where g.student.id = :studentId" +
            " and course.knowledgeControl.id in (:KnowledgeControlIds)" +
            " and course.id in (:courseIds)" +
            " Order by course.courseName.name")
    //TODO cr: для мене sql здається простішим і швидшим і читати його легше. але це більш як рекомендація
    List<Grade> getByStudentIdAndCoursesAndKCTypes(
            @Param("studentId") Integer studentId,
            @Param("courseIds") List<Integer> courseIds,
            @Param("KnowledgeControlIds") List<Integer> knowledgeControlsIds
    );

}

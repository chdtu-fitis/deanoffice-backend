package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForStudent;

import java.util.List;

public interface CoursesForStudentsRepository extends JpaRepository<CourseForStudent, Integer> {
    @Query("select cfs from CourseForStudent cfs " +
            "where cfs.studentDegree.id in :studentDegreeIds and cfs.course.semester=:semester " +
            "order by cfs.studentDegree.id, cfs.course.semester, cfs.course.knowledgeControl.name, cfs.course.courseName.name")
    List<CourseForStudent> getByStudentIdAndSemester(@Param("studentDegreeIds") List<Integer> studentDegreeIds,
                                                    @Param("semester") int semester);

    @Query("select cfs.course from CourseForStudent cfs " +
            "where cfs.studentDegree.id = :studentDegreeId and cfs.courseType ='RECREDIT' " +
            "order by cfs.studentDegree.id, cfs.course.semester, cfs.course.knowledgeControl.name, cfs.course.courseName.name")
    List<Course> getRecreditedByStudentDegreeId(@Param("studentDegreeId") Integer studentDegreeId);

    @Modifying
    @Transactional
    @Query("delete from CourseForStudent cfs where cfs.studentDegree.id = :studentDegreeId and cfs.course.id IN :courseIds")
    int deleteByStudentDegreeIdAndCourseIds(@Param("studentDegreeId") int studentDegreeId, @Param("courseIds") List<Integer> courseIds);
}

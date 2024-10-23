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

    @Query("delete from CourseForStudent cfs where cfs.studentDegree.id = :studentDegreeId and cfs.course.id IN :courseIds")
    @Modifying
    @Transactional
    int deleteByStudentDegreeIdAndCourseIds(@Param("studentDegreeId") int studentDegreeId, @Param("courseIds") List<Integer> courseIds);

    @Query("update CourseForStudent cfs SET cfs.course.id = :courseId where cfs.studentDegree.id = :studentDegreeId and cfs.course.id = :oldCourseId")
    @Modifying
    @Transactional
    int updateByCourseIdAndStudentDegreeId(@Param("studentDegreeId") int studentDegreeId, @Param("courseId") int courseId, @Param("oldCourseId") int oldCourseId);

    @Query("select count(cfs) > 0 from CourseForStudent cfs where cfs.course.id = :courseId and cfs.studentDegree.id = :studentDegreeId")
    boolean existsByCourseIdAndStudentDegreeId(@Param("courseId") int courseId, @Param("studentDegreeId") int studentDegreeId);
}

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

    @Query("delete from CourseForStudent cfs where cfs.studentDegree.id = :studentDegreeId and cfs.course.id = :courseId")
    @Modifying
    @Transactional
    void deleteStudentFromCourseByStudentDegreeIdAndCourseId(@Param("studentDegreeId") int studentDegreeId, @Param("courseId") int courseId);

    @Query("select studentDegree.id from CourseForStudent cfs where cfs.course.id = :courseID")
    List<Integer> getStudentsOnCourseByCourseId(@Param("courseID") int courseID);

    @Query("select count(cfs) > 0 from CourseForStudent cfs where cfs.studentDegree.id = :studentDegreeId")
    boolean existsByStudentDegreeId(@Param("studentDegreeId") int studentDegreeId);

    @Query("select count(cfs) > 0 from CourseForStudent cfs where cfs.course.id = :courseId")
    boolean existsByCourseId(@Param("courseId") int courseId);

}

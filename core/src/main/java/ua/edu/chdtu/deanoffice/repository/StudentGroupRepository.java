package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {

    @Query("select sg from StudentGroup as sg " +
            "where sg.active = true and sg.specialization.faculty.id = :facultyId " +
            "order by sg.name")
    List<StudentGroup> findAllActiveByFaculty(@Param("facultyId") int facultyId);

    @Query("select sg from StudentGroup as sg " +
            "where sg.specialization.faculty.id = :facultyId " +
            "order by sg.name")
    List<StudentGroup> findAllByFaculty(@Param("facultyId") int facultyId);

    @Query("select cfg.studentGroup from CourseForGroup as cfg " +
            "where cfg.course.id = :courseId")
    List<StudentGroup> findAllByCourse(@Param("courseId") int courseId);

   @Query(value = "SELECT * FROM student_group sg " +
           "INNER JOIN specialization s ON s.id = sg.specialization_id " +
           "WHERE sg.active = TRUE AND s.degree_id = :degreeId " +
           "AND floor(sg.creation_year + sg.study_years - 0.1) = :currYear " +
           "ORDER BY sg.tuition_form DESC, sg.name", nativeQuery = true)
    List<StudentGroup> findGraduateByDegree(
            @Param("degreeId") Integer degreeId,
            @Param("currYear") Integer currYear
    );

    @Query("select sg from StudentGroup sg " +
            "where sg.active = true " +
            "and sg.specialization.degree.id = :degree_id " +
            "and :curr_year - sg.creationYear + sg.beginYears = :study_year " +
            "order by sg.tuitionForm desc, sg.name")
   List<StudentGroup> findGroupsByDegreeAndYear(
            @Param("degree_id") Integer degreeId,
            @Param("study_year") Integer studyYear,
            @Param("curr_year") Integer currYear
   );
   @Query("select sg from StudentGroup sg " +
        "where sg.active = true " +
        "and :curr_year - sg.creationYear + sg.beginYears = :study_year " +
        "order by sg.tuitionForm desc, sg.name")
   List<StudentGroup> findGroupsByYear(
        @Param("study_year") Integer studyYear,
        @Param("curr_year") Integer currYear
   );
}

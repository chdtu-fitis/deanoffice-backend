package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface StudentDegreeRepository extends JpaRepository<StudentDegree, Integer>, JpaSpecificationExecutor<StudentDegree> {
    @Query("SELECT sd from StudentDegree sd " +
            "where sd.active = :active " +
            "and sd.specialization.faculty.id = :facultyId " +
            "order by sd.student.surname, sd.student.name, sd.student.patronimic, sd.specialization.name")
    List<StudentDegree> findAllByActive(
            @Param("active") boolean active,
            @Param("facultyId") Integer facultyId
    );

    @Query("SELECT sd from StudentDegree sd " +
            "where sd.id in :student_degree_ids")
    List<StudentDegree> getAllByIds(@Param("student_degree_ids") List<Integer> studentDegreeIds);

    StudentDegree getById(Integer id);

    @Query("SELECT sd FROM StudentDegree sd " +
            "where sd.student.id = :student_id")
    List<StudentDegree> findAllByStudentId(@Param("student_id") Integer studentId);

    @Query("SELECT sd FROM StudentDegree sd " +
            "where sd.student.id = :student_id and sd.active = true")
    List<StudentDegree> findAllActiveByStudentId(@Param("student_id") Integer studentId);

    @Query("select sd from StudentDegree sd " +
            "where sd.studentGroup.id = :groupId and sd.active = :active " +
            "order by sd.student.surname, sd.student.name, sd.student.patronimic")
    List<StudentDegree> findStudentDegreeByStudentGroupIdAndActive(
            @Param("groupId") Integer groupId,
            @Param("active") boolean active
    );

    @Query(value = "SELECT * FROM student_degree sd " +
            "INNER JOIN specialization s ON s.id = sd.specialization_id " +
            "INNER JOIN student_group sg ON sd.student_group_id = sg.id " +
            "INNER JOIN student st ON st.id=sd.student_id " +
            "WHERE sg.active = TRUE and sd.active=true AND s.degree_id = :degree_id " +
            "AND floor(sg.creation_year + sg.study_years - 0.1) = :year " +
            "AND s.faculty_id = :faculty_id " +
            "ORDER BY sg.tuition_form DESC, sg.name", nativeQuery = true)
    List<StudentDegree> findAllGraduates(
            @Param("year") int year,
            @Param("faculty_id") int facultyId,
            @Param("degree_id") int degreeId
    );

    @Query("SELECT sd from StudentDegree sd " +
            "where sd.active = :active " +
            "and sd.student.id = :studentId and sd.specialization.id = :specializationId ")
    StudentDegree findByStudentIdAndSpecialityId(
            @Param("active") boolean active,
            @Param("studentId") Integer studentId,
            @Param("specializationId") Integer specializationId
    );

    @Override
    List<StudentDegree> findAll(Specification<StudentDegree> spec);

    @Modifying
    @Query(value = "UPDATE StudentDegree sd " +
            "SET sd.studentGroup = :group WHERE sd IN (:studentDegrees)")
    void assignStudentsToGroup(@Param("studentDegrees")List<StudentDegree> studentDegrees, @Param("group")StudentGroup group);

}

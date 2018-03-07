package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.List;

public interface StudentDegreeRepository extends JpaRepository<StudentDegree, Integer> {
    @Query("SELECT sd from StudentDegree sd " +
            "where sd.active = :active and sd.studentGroup.specialization.faculty.id = :facultyId " +
            "order by sd.student.surname, sd.student.name, sd.student.patronimic, sd.studentGroup.name")
    List<StudentDegree> findAllByActiveForFacultyId(
            @Param("active") boolean active,
            @Param("facultyId") Integer facultyId
    );

//TODO cr: цей метод точно потрібен? коли практично він може бути використаний?
    @Query("SELECT sd from StudentDegree as sd where sd.id in :student_degree_ids")
    List<StudentDegree> getAllByStudentDegreeIds(@Param("student_degree_ids") Integer[] studentDegreeIds);

    StudentDegree getById(Integer id);
}

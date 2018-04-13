package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;

import java.util.List;

public interface StudentAcademicVacationRepository extends JpaRepository<StudentAcademicVacation, Integer> {
    @Query("select sac from StudentAcademicVacation  sac " +
            "where sac.studentDegree.specialization.faculty.id = :faculty_id " +
            "order by sac.studentDegree.student.surname, sac.studentDegree.student.name, " +
            "sac.studentDegree.student.patronimic, sac.studentDegree.studentGroup.name")
    List<StudentAcademicVacation> findAllByFaculty(@Param("faculty_id") Integer facultyId);

    @Query("select sav from StudentAcademicVacation sav " +
            "where sav.id = :id and sav.studentDegree.active = true")
    StudentAcademicVacation findActiveById(@Param("id") Integer id);
}

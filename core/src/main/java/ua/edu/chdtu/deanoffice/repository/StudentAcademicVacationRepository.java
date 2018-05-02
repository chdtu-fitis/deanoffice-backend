package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;

import java.util.List;

public interface StudentAcademicVacationRepository extends JpaRepository<StudentAcademicVacation, Integer> {
    @Query("select sac from StudentAcademicVacation  sac " +
            "where sac.studentDegree.specialization.faculty.id = :faculty_id " +
            "and sac.studentDegree.active = false " +
            "order by sac.studentDegree.student.surname, sac.studentDegree.student.name, " +
            "sac.studentDegree.student.patronimic, sac.studentDegree.studentGroup.name")
    List<StudentAcademicVacation> findAllInactive(@Param("faculty_id") Integer facultyId);
}

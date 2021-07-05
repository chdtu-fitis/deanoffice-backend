package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("select sac from StudentAcademicVacation sac " +
            "where sac.active = true " +
            "and sac.studentDegree.active = false " +
            "and sac.studentDegree.id in(:studentDegreeIds)")
    List<StudentAcademicVacation> findActive(@Param("studentDegreeIds") List<Integer> studentDegreeIds);

    @Query("select sac from StudentAcademicVacation  sac " +
            "where sac.studentDegree.id =:student_degree_id " +
            "order by sac.vacationStartDate")
    List<StudentAcademicVacation> findAllByDegreeId(@Param("student_degree_id") Integer degreeId);
}

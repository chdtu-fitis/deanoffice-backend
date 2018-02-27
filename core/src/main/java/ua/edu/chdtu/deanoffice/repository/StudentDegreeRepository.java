package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.List;

public interface StudentDegreeRepository extends JpaRepository<StudentDegree, Integer> {
    @Query("SELECT studentDegree from StudentDegree as studentDegree where studentDegree.student.active = :active and studentDegree.student.studentGroup.specialization.faculty.id = :facultyId")
    List<StudentDegree> findAllByFaculty(@Param("active") boolean active, @Param("facultyId") Integer facultyId);
}

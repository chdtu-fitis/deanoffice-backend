package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface GroupRepository extends JpaRepository<StudentGroup, Integer> {
    @Query(value = "SELECT * FROM student_group sg " +
            "INNER JOIN specialization s ON s.id=sg.specialization_id " +
            "WHERE sg.active=TRUE AND s.degree_id=:degreeId AND floor(sg.creation_year+sg.study_years-0.1)=:currYear " +
            "ORDER BY sg.tuition_form DESC, sg.name", nativeQuery = true)
    List<StudentGroup> findGraduateByDegree(@Param("degreeId") Integer degreeId, @Param("currYear") Integer currYear);

    @Query(value = "SELECT * FROM student_group sg " +
            "INNER JOIN specialization s ON s.id=sg.specialization_id " +
            "WHERE sg.active=TRUE AND s.degree_id=:degreeId AND :currYear-sg.creation_year+sg.begin_years=:studyYear " +
            "ORDER BY sg.tuition_form DESC, sg.name", nativeQuery = true)
    List<StudentGroup> findGroupsByDegreeAndYear(@Param("degreeId") Integer degreeId, @Param("studyYear") Integer studyYear, @Param("currYear") Integer currYear);

}

package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface GroupRepository extends JpaRepository<StudentGroup, Integer> {
    @Query(value = "select * from student_group sg inner join specialization s on s.id=sg.specialization_id "+
            "where sg.active=true and s.degree_id=:degreeId and floor(sg.creation_year+sg.study_years-0.1)=:currYear "+
            "order by sg.tuition_form desc, sg.name", nativeQuery = true)
    List<StudentGroup> findGraduateByDegree(@Param("degreeId") Integer degreeId, @Param("currYear") Integer currYear);
}

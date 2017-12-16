package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface GroupRepository extends JpaRepository<StudentGroup, Integer> {
    @Query("select sg from StudentGroup sg join sg.specialization s "+
            "where sg.active=true and s.degree.id=:degreeId "+
            "order by sg.tuitionForm desc, sg.name")
    List<StudentGroup> findGraduateByDegree(@Param("degreeId") Integer degreeId);
}

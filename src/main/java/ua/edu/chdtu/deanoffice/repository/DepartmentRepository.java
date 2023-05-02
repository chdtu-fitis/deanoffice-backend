package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Department;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Query("select d from Department d " +
            "where d.active = :active " +
            "and d.faculty.id = :faculty_id " +
            "order by d.name")
    List<Department> getAllByActive(@Param("active") boolean active, @Param("faculty_id") int facultyId);

    @Query("select d from Department d " +
            "where d.active = :active " +
            "order by d.name")
    List<Department> getAllByActive(@Param("active") boolean active);

    @Query("select d from Department d " +
            "where d.active = :active " +
            "and d.abbr = :abbr" )
    List<Department> getAllByAbbr(@Param("active") boolean active, @Param("abbr") String abbr);
}


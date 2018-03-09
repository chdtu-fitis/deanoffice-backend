package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.edu.chdtu.deanoffice.entity.Degree;

import java.util.List;

public interface DegreeRepository extends JpaRepository<Degree, Integer> {
    @Query("select d from Degree d order by id")
    List<Degree> findAll();
}

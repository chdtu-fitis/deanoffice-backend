package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.ScientificDegree;

import java.util.List;

public interface ScientificDegreeRepository extends JpaRepository<ScientificDegree, Integer> {
    List<ScientificDegree> findAllByOrderByName();
}

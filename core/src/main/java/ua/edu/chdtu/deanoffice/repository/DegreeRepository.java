package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.Degree;

public interface DegreeRepository extends JpaRepository<Degree, Integer> {
}

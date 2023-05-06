package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.CurrentYear;

public interface CurrentYearRepository extends JpaRepository<CurrentYear, Integer> {
}

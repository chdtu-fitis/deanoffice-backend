package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {
}

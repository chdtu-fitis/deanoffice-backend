package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;

@Repository
public interface AcquiredCompetenciesRepository extends JpaRepository<AcquiredCompetencies, Integer> {

    AcquiredCompetencies findBySpecializationIdAndYear(Integer specializationId, Integer year);
}

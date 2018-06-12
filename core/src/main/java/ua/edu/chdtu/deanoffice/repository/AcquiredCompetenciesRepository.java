package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;

public interface AcquiredCompetenciesRepository extends JpaRepository<AcquiredCompetencies, Integer> {

    @Query(value = "select ac.* from acquired_competencies as ac " +
            "where ac.specialization_id = :specialization_id " +
            "order by ac.year desc " +
            "limit 1", nativeQuery = true)
    AcquiredCompetencies findLastCompetenciesForSpecialization(@Param("specialization_id") int specializationId);

    AcquiredCompetencies findBySpecializationIdAndYear(Integer specializationId, Integer year);
}

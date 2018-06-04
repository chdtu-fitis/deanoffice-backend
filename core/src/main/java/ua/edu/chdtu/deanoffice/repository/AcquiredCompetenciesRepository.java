package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;

import java.util.List;

public interface AcquiredCompetenciesRepository extends JpaRepository<AcquiredCompetencies, Integer> {

    @Query("select ac from AcquiredCompetencies ac " +
            "where ac.specialization.id =  :specialization_id " +
            "order by ac.year desc")
    List<AcquiredCompetencies> findLastCompetenciesForSpecialization(@Param("specialization_id") int specializationId);

    AcquiredCompetencies findBySpecializationIdAndYear(Integer specializationId, Integer year);
}

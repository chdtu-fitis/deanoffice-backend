package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;

import java.util.List;

@Repository
public interface QualificationForSpecializationRepository extends JpaRepository<QualificationForSpecialization, Integer> {

    List<QualificationForSpecialization> findAllBySpecializationIdAndYear(Integer specializationId, Integer year);

}

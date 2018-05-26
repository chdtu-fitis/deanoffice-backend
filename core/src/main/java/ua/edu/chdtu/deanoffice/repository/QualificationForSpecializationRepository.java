package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;

@Repository
public interface QualificationForSpecializationRepository extends JpaRepository<QualificationForSpecialization, Integer> {

    QualificationForSpecialization findBySpecializationIdAndYear(Integer specializationId, Integer year);

}

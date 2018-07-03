package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;

import java.util.List;

@Repository
public interface ProfessionalQualificationRepository extends JpaRepository<ProfessionalQualification, Integer> {
    List<ProfessionalQualification> findAllByOrderByName();
}

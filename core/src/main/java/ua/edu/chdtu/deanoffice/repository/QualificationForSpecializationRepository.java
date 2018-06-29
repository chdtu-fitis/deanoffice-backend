package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;

import java.util.List;

@Repository
public interface QualificationForSpecializationRepository extends JpaRepository<QualificationForSpecialization, Integer> {

    @Query(value = "select * " +
            "from qualifications_for_specializations " +
            "where specialization_id = :specializationId " +
            "and year = (" +
            "select year from qualifications_for_specializations where specialization_id = :specializationId" +
            " group by year" +
            " order by year desc" +
            " limit 1)", nativeQuery = true)
    List<QualificationForSpecialization> findAllBySpecializationIdAndYear(@Param("specializationId") Integer specializationId);

    @Query(value = "select qfs.* from qualifications_for_specializations as qfs " +
            "inner join professional_qualification q on qfs.professional_qualification_id = q.id " +
            "where qfs.specialization_id = :specialization_id " +
            "order by qfs.year desc " +
            "limit 1", nativeQuery = true)
    QualificationForSpecialization getLastQualificationBySpecializationId(@Param("specialization_id") int specializationsId);
}

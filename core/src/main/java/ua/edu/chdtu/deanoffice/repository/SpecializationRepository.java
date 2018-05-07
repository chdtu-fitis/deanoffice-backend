package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Specialization;

import java.util.List;

public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {

    @Query("select s from Specialization s " +
            "where s.active = :active " +
            "and s.faculty.id = :faculty_id " +
            "order by s.name, s.degree.id")
    List<Specialization> findAllByActive(@Param("active") boolean active, @Param("faculty_id") int facultyId);
}

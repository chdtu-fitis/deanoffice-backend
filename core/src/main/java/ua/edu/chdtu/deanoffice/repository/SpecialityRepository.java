package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.Speciality;

import java.util.List;

public interface SpecialityRepository extends JpaRepository<Speciality, Integer> {
    List<Speciality> findAllByActiveOrderByName(boolean active);
}

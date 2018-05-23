package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Speciality;

import java.util.List;

public interface SpecialityRepository extends JpaRepository<Speciality, Integer> {
    @Query(value =
            "select s.* from speciality as s " +
            "inner join specialization as sz on s.id = sz.speciality_id " +
            "where sz.faculty_id = :faculty_id and s.active = true " +
            "group by s.id " +
            "order by s.name, s.code",
            nativeQuery = true)
    List<Speciality> findAllActive(@Param("faculty_id") int facultyId);

    @Query(value =
            "select s.* from speciality as s " +
            "inner join specialization as sz on s.id = sz.speciality_id " +
            "where sz.faculty_id = :faculty_id " +
            "group by s.id " +
            "order by s.name, s.code",
            nativeQuery = true)
    List<Speciality> findAll(@Param("faculty_id") int facultyId);

    List<Speciality> findAllByActiveOrderByName(boolean active);

    Speciality getSpecialityByName(String name);

    Speciality getSpecialityByCode(String code);
}

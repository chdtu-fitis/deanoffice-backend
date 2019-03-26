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
            "order by s.name, s.speciality.name, s.degree.id")
    List<Specialization> findAllByActive(@Param("active") boolean active, @Param("faculty_id") int facultyId);

    @Query("select s from  Specialization s " +
            "where s.active = :active " +
            "and s.faculty.id = :faculty_id " +
            "and s.degree.id = :degree_id " +
            "order by s.name, s.speciality.name, s.degree.id")
    List<Specialization> findAllByActiveAndDegree(@Param("active") boolean active,
                                                  @Param("faculty_id") int facultyId,
                                                  @Param("degree_id") int degreeId);

    @Query("select s from Specialization s " +
            "where s.id in :specialization_ids")
    List<Specialization> findAllByIds(@Param("specialization_ids") Integer[] specializationIds);

    @Query("select s from Specialization s " +
            "where upper(s.name) = upper(:name) " +
            "and s.degree.id = :degree_id " +
            "and s.speciality.id = :speciality_id " +
            "and s.faculty.id = :faculty_id")
    List<Specialization> findByNameAndDegreeAndSpecialityAndFaculty(@Param("name")String name, @Param("degree_id") Integer degreeId,
                                                                    @Param("speciality_id") Integer specialityId, @Param("faculty_id") Integer facultyId);

    @Query("select s from Specialization s " +
            "where s.speciality.id = :speciality_id " +
            "and s.faculty.id = :faculty_id")
    List<Specialization> findAllBySpecialityId(@Param("speciality_id") int specialityId, @Param("faculty_id") int facultyId);
}

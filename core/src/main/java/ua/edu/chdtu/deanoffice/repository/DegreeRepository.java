package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Degree;

import java.util.List;

public interface DegreeRepository extends JpaRepository<Degree, Integer> {
    @Query("select d from Degree d order by id")
    List<Degree> findAll();

    @Query("select d from Degree d where upper(d.name)=upper(:name) ")
    Degree findByName(@Param("name") String name);

    @Query(value =
            "SELECT max(study_semesters) FROM student_group AS sg " +
            "INNER JOIN specialization AS s ON sg.specialization_id = s.id " +
            "INNER JOIN degree AS d ON s.degree_id = d.id " +
            "WHERE d.name_eng = :nameEng AND sg.active = true " +
            "AND s.faculty_id = :facultyId", nativeQuery = true)
    int findMaxSemesterForDegreeByNameEngAndFacultyId(@Param("nameEng") String nameEnd,
                                                      @Param("facultyId") int facultyId);
}

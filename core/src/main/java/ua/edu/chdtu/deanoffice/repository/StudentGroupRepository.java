package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

/**
 * Created by os199 on 05.11.2017.
 */
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer>{

    @Query("SELECT sg.id, sg.name, sg.studySemesters FROM StudentGroup AS sg INNER JOIN sg.specialization ON sg.id = sg.specialization.id " +
            "inner join sg.specialization.faculty on sg.specialization.faculty.id = sg.specialization.id "+
            "WHERE sg.active = 'T' and sg.specialization.faculty.id = :facultyId")
    List<StudentGroup> findAllBySpecialization(@Param("facultyId") int facultyId);


}

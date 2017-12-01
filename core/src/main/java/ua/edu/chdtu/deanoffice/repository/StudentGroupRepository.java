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

    @Query("select sg from StudentGroup as sg where sg.active = true and sg.specialization.faculty.id = :facultyId")
    List<StudentGroup> findAllByFaculty(@Param("facultyId") int facultyId);


}

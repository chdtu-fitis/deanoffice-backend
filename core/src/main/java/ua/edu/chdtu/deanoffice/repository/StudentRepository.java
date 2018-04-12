package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Query("select s from Student s " +
            "where s.name like %:name% and s.surname like %:surname% and s.patronimic like %:patronimic%")
    List<Student> findAllByFullNameUkr(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("patronimic") String patronimic
    );
}

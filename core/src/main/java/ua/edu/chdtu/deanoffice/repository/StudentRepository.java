package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Student;

import java.util.Date;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Query("select s from StudentDegree sd " +
            "join sd.student s " +
            "where s.name like %:name% " +
            "and s.surname like %:surname% " +
            "and s.patronimic like %:patronimic% " +
            "group by s.id " +
            "order by s.name, s.surname, s.patronimic")
    List<Student> findAllByFullNameUkr(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("patronimic") String patronimic
    );

//    @Query("select s from Student s " +
//            "where s.name = :name " +
//            "and s.surname = :surname " +
//            "and s.patronimic = :patronimic " +
//            "order by s.surname, s.name, s.patronimic")
//    List<Student> findAllByFullNameUkr(
//            @Param("name") String name,
//            @Param("surname") String surname,
//            @Param("patronimic") String patronimic
//            );

    @Query("select s from Student s " +
            "where s.name = :name " +
            "and s.surname = :surname " +
            "and s.patronimic = :patronimic " +
            "and s.birthDate = :birthDate")
    List<Student> findByFullNameUkrAndBirthDate(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("patronimic") String patronimic,
            @Param("birthDate") Date birthDate
    );
}

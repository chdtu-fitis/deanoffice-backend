package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    //TODO cr: 1. Потрібно структурувати запити.
    //2. Для чого вказуєте "as" в запитах? Чим це допомагає зробити роботу програми кращою?
    //3. Аліаси на те й аліаси, щоб бути короткими (пригадайте лекції з ОБДЗ)
    @Query("select student from Student as student where student.id in :student_ids")
    List<Student> getAllByStudentIds(@Param("student_ids") Integer[] studentIds);

    @Query("select student from Student as student where student.name like %:name% and student.surname like %:surname% and student.patronimic like %:patronimic%")
    List<Student> findAllByFullNameUkr(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("patronimic") String patronimic
    );
}

package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> getByStudentGroupAndActiveOrderBySurnameAsc(StudentGroup group, Boolean isActive);

    List<Student> getByStudentGroupIdAndActiveOrderBySurnameAsc(Integer id, Boolean isActive);
}

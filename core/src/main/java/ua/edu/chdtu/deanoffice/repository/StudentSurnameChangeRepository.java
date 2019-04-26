package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.StudentSurnameChange;

import java.util.List;

public interface StudentSurnameChangeRepository extends JpaRepository<StudentSurnameChange, Integer> {

    List<StudentSurnameChange> findAllByFacultyId(int id);
}

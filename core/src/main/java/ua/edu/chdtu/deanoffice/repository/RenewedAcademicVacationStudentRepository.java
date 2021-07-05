package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.RenewedAcademicVacationStudent;

public interface RenewedAcademicVacationStudentRepository extends JpaRepository<RenewedAcademicVacationStudent, Integer> {
    RenewedAcademicVacationStudent findByStudentAcademicVacationId(int id);
}

package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}

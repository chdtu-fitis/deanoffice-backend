package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {
    Privilege findPrivilegeByName(String active);
}

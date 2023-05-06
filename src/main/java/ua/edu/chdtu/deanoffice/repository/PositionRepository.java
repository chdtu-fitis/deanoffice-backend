package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Integer> {

}


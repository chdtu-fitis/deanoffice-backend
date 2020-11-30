package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.FieldOfKnowledge;

public interface FieldOfKnowledgeRepository extends JpaRepository<FieldOfKnowledge, Integer> {
}

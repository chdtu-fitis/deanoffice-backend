package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;

public interface KnowledgeControlRepository extends JpaRepository<KnowledgeControl, Integer> {
}
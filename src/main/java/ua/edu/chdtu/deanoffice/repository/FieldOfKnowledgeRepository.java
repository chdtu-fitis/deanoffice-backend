package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.FieldOfKnowledge;

import java.util.List;

public interface FieldOfKnowledgeRepository extends JpaRepository<FieldOfKnowledge, Integer> {
   List<FieldOfKnowledge> findAllByCode(String code);
}

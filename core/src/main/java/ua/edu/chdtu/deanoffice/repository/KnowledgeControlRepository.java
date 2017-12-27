package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;

import java.util.List;

public interface KnowledgeControlRepository  extends JpaRepository<KnowledgeControl, Integer> {
    @Query
    List<KnowledgeControl> findKnowledgeControlById(@Param("knowledgeControlId") int knowledgeControlId);
}
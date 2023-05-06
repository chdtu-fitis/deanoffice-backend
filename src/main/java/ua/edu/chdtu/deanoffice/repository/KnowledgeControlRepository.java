package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;

public interface KnowledgeControlRepository extends JpaRepository<KnowledgeControl, Integer> {
    @Query("SELECT graded from KnowledgeControl kc " +
            "where kc.id = :kc_id ")
    boolean findGradedByKnowledgeControlId(@Param("kc_id") int kc_id);
}
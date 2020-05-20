package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.edu.chdtu.deanoffice.entity.order.LegalBasis;

import java.util.List;

public interface LegalBasisRepository extends JpaRepository<LegalBasis, Integer> {
    @Query("select lb from LegalBasis as lb " +
            "where lb.active = true")
    LegalBasis findActive();

    @Query("select lb from LegalBasis as lb " +
            "where lb.active = false")
    List<LegalBasis> findNotActive();
}

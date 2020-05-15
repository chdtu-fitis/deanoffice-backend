package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.order.LegalBasis;

public interface LegalBasisRepository extends JpaRepository<LegalBasis, Integer> {
    LegalBasis findByActive(boolean active);
}

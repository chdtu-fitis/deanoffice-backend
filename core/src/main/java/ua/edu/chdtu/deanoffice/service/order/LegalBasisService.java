package ua.edu.chdtu.deanoffice.service.order;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.order.LegalBasis;
import ua.edu.chdtu.deanoffice.repository.order.LegalBasisRepository;

@Service
public class LegalBasisService {
    private LegalBasisRepository legalBasisRepository;

    public LegalBasisService(LegalBasisRepository legalBasisRepository) {
        this.legalBasisRepository = legalBasisRepository;
    }

    public LegalBasis getActiveLegalBasis() {
        return legalBasisRepository.findByActive(true);
    }
}

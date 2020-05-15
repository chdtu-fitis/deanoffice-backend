package ua.edu.chdtu.deanoffice.api.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.entity.order.LegalBasis;
import ua.edu.chdtu.deanoffice.service.order.LegalBasisService;

@RestController("/orders")
public class LegalBasisController {
    private LegalBasisService legalBasisService;

    public LegalBasisController(LegalBasisService legalBasisService) {
        this.legalBasisService = legalBasisService;
    }

    @GetMapping("/legal-basis")
    public ResponseEntity<LegalBasis> getActiveLegalBasis() {
        LegalBasis legalBasis = legalBasisService.getActiveLegalBasis();
        return ResponseEntity.ok(legalBasis);
    }
}

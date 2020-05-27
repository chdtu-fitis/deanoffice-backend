package ua.edu.chdtu.deanoffice.api.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.order.dto.LegalBasisDTO;
import ua.edu.chdtu.deanoffice.entity.order.LegalBasis;
import ua.edu.chdtu.deanoffice.service.order.LegalBasisService;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController("/orders")
public class LegalBasisController {
    private LegalBasisService legalBasisService;

    public LegalBasisController(LegalBasisService legalBasisService) {
        this.legalBasisService = legalBasisService;
    }

    @GetMapping("/legal-basis")
    public ResponseEntity<LegalBasisDTO> getActiveLegalBasis() {
        LegalBasis legalBasis = legalBasisService.getActiveLegalBasis();
        return ResponseEntity.ok(strictMap(legalBasis, LegalBasisDTO.class));
    }

    @GetMapping("/legal-basis-old")
    public ResponseEntity<List<LegalBasisDTO>> getNotActiveLegalBasis() {
        List<LegalBasis> legalBasis = legalBasisService.getNotActiveLegalBasis();
        return ResponseEntity.ok(strictMap(legalBasis, LegalBasisDTO.class));
    }
}

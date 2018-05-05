package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.dto.KnowledgeControlDTO;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.service.KnowledgeControlService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/knowledgeControls")
public class KnowledgeControlController {
    private final KnowledgeControlService knowledgeControlService;

    @Autowired
    public KnowledgeControlController(KnowledgeControlService knowledgeControlService) {
        this.knowledgeControlService = knowledgeControlService;
    }

    @GetMapping
    public ResponseEntity getAllKnowledgeControls() {
        List<KnowledgeControl> knowledgeControlDTOs = this.knowledgeControlService.getAllKnowledgeControls();
        return ResponseEntity.ok(map(knowledgeControlDTOs, KnowledgeControlDTO.class));
    }

}

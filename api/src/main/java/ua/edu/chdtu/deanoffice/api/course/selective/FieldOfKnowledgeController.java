package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.FieldOfKnowledgeDTO;
import ua.edu.chdtu.deanoffice.entity.FieldOfKnowledge;
import ua.edu.chdtu.deanoffice.service.course.selective.FieldOfKnowledgeService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/field-of-knowledge")
public class FieldOfKnowledgeController {

    private FieldOfKnowledgeService fieldOfKnowledgeService;

    public FieldOfKnowledgeController(FieldOfKnowledgeService fieldOfKnowledgeService) {
        this.fieldOfKnowledgeService = fieldOfKnowledgeService;
    }

    @GetMapping
    public ResponseEntity getFieldsOfKnowledgeByIds() {
        List<FieldOfKnowledge> fieldOfKnowledge = fieldOfKnowledgeService.getFieldsOfKnowledge();
        return ResponseEntity.ok(map(fieldOfKnowledge, FieldOfKnowledgeDTO.class));
    }
}

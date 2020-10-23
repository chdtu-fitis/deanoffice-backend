package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.FieldOfKnowledge;
import ua.edu.chdtu.deanoffice.repository.FieldOfKnowledgeRepository;

import java.util.List;

@Service
public class FieldOfKnowledgeService {

    private FieldOfKnowledgeRepository fieldOfKnowledgeRepository;

    public FieldOfKnowledgeService(FieldOfKnowledgeRepository fieldOfKnowledgeRepository) {
        this.fieldOfKnowledgeRepository = fieldOfKnowledgeRepository;
    }

    public FieldOfKnowledge getFieldOfKnowledgeById(int id) {
        return fieldOfKnowledgeRepository.findOne(id);
    }

    public List<FieldOfKnowledge> getFieldsOfKnowledge() {
        return fieldOfKnowledgeRepository.findAll();
    }

    public List<FieldOfKnowledge> getFieldsOfKnowledge(List<Integer> ids) {
        return fieldOfKnowledgeRepository.findAll(ids);
    }
}

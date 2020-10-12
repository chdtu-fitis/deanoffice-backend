package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.FieldOfKnowledge;
import ua.edu.chdtu.deanoffice.repository.FieldOfKnowledgeRepository;

@Service
public class FieldOfKnowledgeService {

    private FieldOfKnowledgeRepository fieldOfKnowledgeRepository;

    public FieldOfKnowledgeService(FieldOfKnowledgeRepository fieldOfKnowledgeRepository) {
        this.fieldOfKnowledgeRepository = fieldOfKnowledgeRepository;
    }

    public FieldOfKnowledge getFieldOfKnowledgeById(int id) {
        return fieldOfKnowledgeRepository.findOne(id);
    }
}

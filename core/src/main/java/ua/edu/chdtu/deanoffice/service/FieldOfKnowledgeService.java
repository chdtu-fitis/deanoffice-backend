package ua.edu.chdtu.deanoffice.service;

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

    public FieldOfKnowledge getFieldOfKnowledgeByCode(String code) {
        List<FieldOfKnowledge> fieldsOfKnowledge = fieldOfKnowledgeRepository.findAllByCode(code);
        if (fieldsOfKnowledge.size() == 1) {
            return fieldsOfKnowledge.get(0);
        }
        return null;
    }

    public FieldOfKnowledge getFieldOfKnowledgeById(int id) {
        return fieldOfKnowledgeRepository.findById(id).get();
    }

    public List<FieldOfKnowledge> getFieldsOfKnowledge() {
        return fieldOfKnowledgeRepository.findAll();
    }

    public List<FieldOfKnowledge> getFieldsOfKnowledge(List<Integer> ids) {
        return fieldOfKnowledgeRepository.findAllById(ids);
    }
}

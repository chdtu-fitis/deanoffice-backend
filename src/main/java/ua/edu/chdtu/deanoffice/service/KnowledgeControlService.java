package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.repository.KnowledgeControlRepository;

import java.util.List;

@Service
public class KnowledgeControlService {
    private final KnowledgeControlRepository knowledgeControlRepository;

    @Autowired
    public KnowledgeControlService(KnowledgeControlRepository knowledgeControlRepository) {
        this.knowledgeControlRepository = knowledgeControlRepository;
    }

    public List<KnowledgeControl> getAllKnowledgeControls() {
        return this.knowledgeControlRepository.findAll();
    }

    public boolean getGradedByKnowledgeControlId(int knowledgeControlId){
        return knowledgeControlRepository.findGradedByKnowledgeControlId(knowledgeControlId);
    }
}

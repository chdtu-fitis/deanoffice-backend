package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.repository.KnowledgeControlRepository;

import java.util.List;

@Service
public class KnowledgeControlService {
    @Autowired
    private KnowledgeControlRepository knowledgeControlRepository;

    public List<KnowledgeControl> getAllKnowledgeControls() {
        return this.knowledgeControlRepository.findAll();
    }
}

package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ScientificDegree;
import ua.edu.chdtu.deanoffice.repository.ScientificDegreeRepository;
import java.util.List;

@Service
public class ScientificDegreeService {
    private final ScientificDegreeRepository scientificDegreeRepository;

    public ScientificDegreeService(ScientificDegreeRepository scientificDegreeRepository) {
        this.scientificDegreeRepository = scientificDegreeRepository;
    }

    public List<ScientificDegree> getScientificDegrees() {
        return scientificDegreeRepository.findAllByOrderByName();
    }
}

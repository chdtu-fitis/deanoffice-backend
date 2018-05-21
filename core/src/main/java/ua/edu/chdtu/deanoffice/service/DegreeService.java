package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.repository.DegreeRepository;

import java.util.List;

@Service
public class DegreeService {
    private final DegreeRepository degreeRepository;

    public DegreeService(DegreeRepository degreeRepository) {
        this.degreeRepository = degreeRepository;
    }

    public List<Degree> getDegrees() {
        return degreeRepository.findAll();
    }

    public Degree getById(Integer degreeId) {
        return degreeRepository.findOne(degreeId);
    }

    public Degree getByName(String degreeName) {
        return degreeRepository.findByName(degreeName);
    }
}

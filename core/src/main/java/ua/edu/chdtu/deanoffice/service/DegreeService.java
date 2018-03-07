package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.repository.DegreeRepository;

import java.util.List;

@Service
public class DegreeService {
    @Autowired
    private DegreeRepository degreeRepository;

    public List<Degree> getDegrees() {
        return degreeRepository.findAll();
    }

    public Degree getDegree(Integer degreeId) {
        return degreeRepository.getOne(degreeId);
    }
}

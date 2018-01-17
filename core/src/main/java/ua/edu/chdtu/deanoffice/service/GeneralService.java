package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.repository.DegreeRepository;

import java.util.List;

@Service
public class GeneralService {
    @Autowired
    private DegreeRepository degreeRepository;

    //TODO cr: не знаю на скільки великими можуть бути тут данні можливо додати пейжинейшн?
    public List<Degree> getDegrees() {
        return degreeRepository.findAll();
    }
}

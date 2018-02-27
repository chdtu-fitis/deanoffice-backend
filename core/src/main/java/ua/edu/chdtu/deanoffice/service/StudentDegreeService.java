package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;

    public StudentDegreeService(StudentDegreeRepository studentDegreeRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
    }

    public StudentDegree getById(Integer id) {
        return studentDegreeRepository.getById(id);
    }

}

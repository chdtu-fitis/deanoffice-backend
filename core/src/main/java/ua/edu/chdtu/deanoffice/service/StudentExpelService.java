package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentExpelRepository;

import java.util.List;

@Service
public class StudentExpelService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final StudentExpelRepository studentExpelRepository;

    @Autowired
    public StudentExpelService(
            StudentDegreeRepository studentDegreeRepository,
            StudentExpelRepository studentExpelRepository
    ) {
        this.studentExpelRepository = studentExpelRepository;
        this.studentDegreeRepository = studentDegreeRepository;
    }

    public boolean studentIsNotExpelled(int studentDegreeId) {
        List<StudentExpel> studentExpels = studentExpelRepository.findAllActiveByStudentDegreeId(studentDegreeId);
        return studentExpels.isEmpty();
    }

    public void returnStudent(int studentDegreeId) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        studentDegree.setActive(true);
        studentDegreeRepository.save(studentDegree);
    }
}

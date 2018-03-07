package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.DegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.util.List;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;

    public StudentDegreeService(StudentDegreeRepository studentDegreeRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
    }

    public StudentDegree getById(Integer id) {
        return studentDegreeRepository.getById(id);
    }

    public List<StudentDegree> findAllByActiveId(boolean active) {
        return studentDegreeRepository.findAllByActiveForFacultyId(active, getCurrentFaculty());
    }

    public List<StudentDegree> findAllByStudentDegreeIds(Integer[] id) {
        return studentDegreeRepository.getAllByStudentDegreeIds(id);
    }

    public StudentDegree save(StudentDegree studentDegree) {
        return this.studentDegreeRepository.save(studentDegree);
    }

    private Integer getCurrentFaculty() {
        return 1;
    }
}

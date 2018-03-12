package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import static ua.edu.chdtu.deanoffice.Constants.FACULTY_ID;

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

    public List<StudentDegree> getAllByActive(boolean active) {
        return studentDegreeRepository.findAllByActiveForFacultyId(active, FACULTY_ID);
    }

    public List<StudentDegree> findAllByIds(Integer[] id) {
        return studentDegreeRepository.getAllByIds(id);
    }

    public StudentDegree save(StudentDegree studentDegree) {
        return this.studentDegreeRepository.save(studentDegree);
    }

    public StudentDegree getFirstStudentDegree(Integer studentId) {
        List<StudentDegree> studentDegreeList = this.studentDegreeRepository.findByStudentId(studentId);
        return (studentDegreeList.isEmpty()) ? null : studentDegreeList.get(0);
    }
}

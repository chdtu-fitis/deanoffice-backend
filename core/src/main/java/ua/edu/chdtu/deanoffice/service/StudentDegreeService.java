package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.util.List;

import static ua.edu.chdtu.deanoffice.Constants.FACULTY_ID;

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

    public StudentDegree save(StudentDegree studentDegree) {
        return this.studentDegreeRepository.save(studentDegree);
    }

    public void update(List<StudentDegree> studentDegree) {
        studentDegreeRepository.save(studentDegree);
    }

    public List<StudentDegree> getStudentDegree(Integer studentId) {
        return this.studentDegreeRepository.findByStudentId(studentId);
    }

    public StudentDegree getFirstStudentDegree(Integer studentId) {
        List<StudentDegree> studentDegreeList = this.studentDegreeRepository.findByStudentId(studentId);
        return (studentDegreeList.isEmpty()) ? null : studentDegreeList.get(0);
    }

    public List<StudentDegree> findStudentsByGroupId(Integer groupId) {
        return this.studentDegreeRepository.findStudentDegreeByStudentGroupIdAndActive(groupId, true);
    }
}

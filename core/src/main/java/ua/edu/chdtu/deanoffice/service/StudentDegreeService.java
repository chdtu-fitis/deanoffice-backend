package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentExpelRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.Constants.FACULTY_ID;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final StudentExpelRepository studentExpelRepository;

    public StudentDegreeService(
            StudentDegreeRepository studentDegreeRepository,
            StudentExpelRepository studentExpelRepository
    ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentExpelRepository = studentExpelRepository;
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

    public void update(List<StudentDegree> studentDegreeSet) {
        studentDegreeRepository.save(studentDegreeSet);
    }

    public StudentDegree getFirstStudentDegree(Integer studentId) {
        List<StudentDegree> studentDegreeList = this.studentDegreeRepository.findByStudentId(studentId);
        return (studentDegreeList.isEmpty()) ? null : studentDegreeList.get(0);
    }

    public List<StudentExpel> expelStudents(List<StudentExpel> studentExpels) {
        List<Integer> idList= studentExpels.stream().map(studentExpel -> studentExpel.getStudentDegree().getId()).collect(Collectors.toList());

        List<StudentDegree> studentDegrees = studentDegreeRepository.getAllByIds(idList);
        studentDegrees.forEach(studentDegree -> studentDegree.setActive(false));
        studentDegreeRepository.save(studentDegrees);

        return studentExpelRepository.save(studentExpels);
    }

    public List<StudentExpel> getAllExpelStudents() {
        return this.studentExpelRepository.findAll();
    }
}

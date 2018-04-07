package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentExpelRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.Constants.FACULTY_ID;
import static ua.edu.chdtu.deanoffice.Constants.SUCCESS_REASON_IDS;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final StudentExpelRepository studentExpelRepository;
    private final CurrentYearRepository currentYearRepository;

    public StudentDegreeService(
            StudentDegreeRepository studentDegreeRepository,
            StudentExpelRepository studentExpelRepository,
            CurrentYearRepository currentYearRepository
    ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentExpelRepository = studentExpelRepository;
        this.currentYearRepository = currentYearRepository;
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
        List<Integer> ids = studentExpels.stream()
                .map(studentExpel -> studentExpel.getStudentDegree().getId())
                .collect(Collectors.toList());

        List<StudentDegree> studentDegrees = studentDegreeRepository.getAllByIds(ids);
        studentDegrees.forEach(studentDegree -> studentDegree.setActive(false));
        studentDegreeRepository.save(studentDegrees);

        return studentExpelRepository.save(studentExpels);
    }

    public List<StudentExpel> getAllExpelStudents(Integer facultyId) {
        int currentYear = currentYearRepository.getOne(1).getCurrYear();
        Date limitYear = new Date(Date.parse((currentYear - 5) + "/01/01"));
        return this.studentExpelRepository.findAllFired(SUCCESS_REASON_IDS, limitYear, facultyId);
    }
}

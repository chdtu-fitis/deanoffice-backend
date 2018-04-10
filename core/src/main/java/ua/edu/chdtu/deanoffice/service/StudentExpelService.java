package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentExpelRepository;

import java.util.List;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;

import java.util.Date;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.Constants.EXPELLED_STUDENTS_YEARS_FOR_INITIAL_VIEW;
import static ua.edu.chdtu.deanoffice.Constants.SUCCESS_REASON_IDS;

@Service
public class StudentExpelService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final StudentExpelRepository studentExpelRepository;
    private final CurrentYearRepository currentYearRepository;

    @Autowired
    public StudentExpelService(
            StudentDegreeRepository studentDegreeRepository,
            StudentExpelRepository studentExpelRepository,
            CurrentYearRepository currentYearRepository
    ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentExpelRepository = studentExpelRepository;
        this.currentYearRepository = currentYearRepository;
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

    public List<StudentExpel> getAllExpelledStudents(Integer facultyId) {
        return this.studentExpelRepository.findAllFired(SUCCESS_REASON_IDS, getLimitDate(), facultyId);
    }

    private Date getLimitDate() {
        int currentYear = currentYearRepository.getOne(1).getCurrYear();
        return new Date((currentYear - EXPELLED_STUDENTS_YEARS_FOR_INITIAL_VIEW) + "/01/01");
    }

    public boolean studentIsNotExpelled(int studentDegreeId) {
        List<StudentExpel> studentExpels = studentExpelRepository.findAllActiveByStudentDegreeId(studentDegreeId);
        return studentExpels.isEmpty();
    }

    public void resumeExpelledStudent(int studentDegreeId) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        studentDegree.setActive(true);
        studentDegreeRepository.save(studentDegree);
    }
}

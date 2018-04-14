package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.RenewedExpelledStudent;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.repository.RenewedExpelledStudentRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentExpelRepository;

import java.util.List;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;

import java.util.Date;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.Constants.EXPELLED_STUDENTS_YEARS_FOR_INITIAL_VIEW;
import static ua.edu.chdtu.deanoffice.Constants.SUCCESS_REASON_IDS;
import static ua.edu.chdtu.deanoffice.util.StudentUtil.studentDegreeToActive;

@Service
public class StudentExpelService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final StudentExpelRepository studentExpelRepository;
    private final CurrentYearRepository currentYearRepository;
    private final RenewedExpelledStudentRepository renewedExpelledStudentRepository;

    @Autowired
    public StudentExpelService(
            StudentDegreeRepository studentDegreeRepository,
            StudentExpelRepository studentExpelRepository,
            CurrentYearRepository currentYearRepository,
            RenewedExpelledStudentRepository renewedExpelledStudentRepository
    ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentExpelRepository = studentExpelRepository;
        this.currentYearRepository = currentYearRepository;
        this.renewedExpelledStudentRepository = renewedExpelledStudentRepository;
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

    public boolean isNotExpelled(int studentExpelId) {
        StudentExpel studentExpel = studentExpelRepository.findInactiveById(studentExpelId);
        return studentExpel == null;
    }

    public boolean isExpelled(Integer[] studentDegreeIds) {
        List<StudentExpel> studentExpels = studentExpelRepository.findAllActiveFired(studentDegreeIds);
        return !studentExpels.isEmpty();
    }

    public RenewedExpelledStudent renew(RenewedExpelledStudent renewedExpelledStudent) {
        Integer studentDegreeId = renewedExpelledStudent.getStudentExpel().getStudentDegree().getId();
        studentDegreeToActive(studentDegreeId, studentDegreeRepository);

        return renewedExpelledStudentRepository.save(renewedExpelledStudent);
    }

    public StudentExpel getById(Integer studentExpelId) {
        return studentExpelRepository.getOne(studentExpelId);
    }
}

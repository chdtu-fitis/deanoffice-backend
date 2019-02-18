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
import ua.edu.chdtu.deanoffice.util.StudentUtil;

import java.util.Date;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.Constants.EXPELLED_STUDENTS_YEARS_FOR_INITIAL_VIEW;
import static ua.edu.chdtu.deanoffice.Constants.SUCCESS_REASON_IDS;

@Service
public class StudentExpelService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final StudentExpelRepository studentExpelRepository;
    private final CurrentYearRepository currentYearRepository;
    private final RenewedExpelledStudentRepository renewedExpelledStudentRepository;
    private final StudentUtil studentUtil;

    @Autowired
    public StudentExpelService(
            StudentDegreeRepository studentDegreeRepository,
            StudentExpelRepository studentExpelRepository,
            CurrentYearRepository currentYearRepository,
            RenewedExpelledStudentRepository renewedExpelledStudentRepository,
            StudentUtil studentUtil
    ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentExpelRepository = studentExpelRepository;
        this.currentYearRepository = currentYearRepository;
        this.renewedExpelledStudentRepository = renewedExpelledStudentRepository;
        this.studentUtil = studentUtil;
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

    public List<Integer> isExpelled(Integer[] studentDegreeIds) {
        List<StudentExpel> studentExpels = studentExpelRepository.findAllActiveFired(studentDegreeIds);
        return studentExpels.stream()
                .map(studentExpel -> studentExpel.getStudentDegree().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    public RenewedExpelledStudent renew(RenewedExpelledStudent renewedExpelledStudent) {
        Integer studentDegreeId = renewedExpelledStudent.getStudentExpel().getStudentDegree().getId();
        studentUtil.studentDegreeToActive(studentDegreeId);
        updateStudentDegree(renewedExpelledStudent);
        return renewedExpelledStudentRepository.save(renewedExpelledStudent);
    }

    private void updateStudentDegree(RenewedExpelledStudent renewedExpelledStudent) {
        StudentDegree studentDegree = renewedExpelledStudent.getStudentExpel().getStudentDegree();

        studentDegree.setPayment(renewedExpelledStudent.getPayment());
        studentDegree.setStudentGroup(renewedExpelledStudent.getStudentGroup());

        studentDegreeRepository.save(studentDegree);
    }

    public StudentExpel getById(Integer studentExpelId) {
        return studentExpelRepository.getOne(studentExpelId);
    }

    public List<StudentExpel> getSpecificationName(Date startDate, Date endDate,String surname, String name, int facultyId) {
        return studentExpelRepository.findAll(StudentDegreeSpecification.getExpelStudent(startDate,endDate,surname, name, facultyId));
    }

    public List <StudentExpel> getByStudentDegreeId(Integer studentDegreeId){
        List <StudentExpel> expelledStudentInformation = studentExpelRepository.findByStudentDegreeIdOrderByExpelDate(studentDegreeId);
        return expelledStudentInformation;
    }

}

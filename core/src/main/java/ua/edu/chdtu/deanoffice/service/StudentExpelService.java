package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.repository.RenewedExpelledStudentRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentExpelRepository;

import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.util.StudentUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final OrderReasonService orderReasonService;

    @Autowired
    public StudentExpelService(
            StudentDegreeRepository studentDegreeRepository,
            StudentExpelRepository studentExpelRepository,
            CurrentYearRepository currentYearRepository,
            RenewedExpelledStudentRepository renewedExpelledStudentRepository,
            StudentUtil studentUtil,
            OrderReasonService orderReasonService
    ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentExpelRepository = studentExpelRepository;
        this.currentYearRepository = currentYearRepository;
        this.renewedExpelledStudentRepository = renewedExpelledStudentRepository;
        this.studentUtil = studentUtil;
        this.orderReasonService = orderReasonService;
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

    @Transactional
    public void expelStudents(List<StudentDegree> studentDegrees, Date expelDate, Date orderDate, String orderNumber) {
        List<StudentExpel> studentExpels = new ArrayList<>();
        studentDegrees.forEach(studentDegree -> studentDegree.setActive(false));
        studentDegreeRepository.save(studentDegrees);

        Map<Integer, OrderReason> orderReasonMap = new HashMap<>();
        orderReasonMap.put(1, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_BACHELOR));
        orderReasonMap.put(2, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_SPECIALIST));
        orderReasonMap.put(3, orderReasonService.getById(Constants.ID_SUCCESSFUL_END_MASTER));

        int currentYear = currentYearRepository.getOne(1).getCurrYear();
        for (StudentDegree studentDegree : studentDegrees) {
            studentExpels.add(new StudentExpel( studentDegree, studentDegree.getStudentGroup(),
                    currentYear - studentDegree.getStudentGroup().getCreationYear() + studentDegree.getStudentGroup().getBeginYears(),
                    studentDegree.getPayment(), expelDate, orderNumber, orderDate,
                    orderReasonMap.get(studentDegree.getSpecialization().getDegree().getId()), null));
        }

        studentExpelRepository.save(studentExpels);
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

    public int getCountStudentsInStudentGroupIdWhoExpelAfterSessionStartDateAndByPayment(int studentGroupId, LocalDate sessionStartDate, Payment payment) {
        return studentExpelRepository.findCountStudentsInStudentGroupIdWhoExpelAfterSessionStartDateAndByPayment(studentGroupId, java.sql.Date.valueOf(sessionStartDate), payment.toString());
    }

    public int getCountStudentsInStudentGroupWhoExpelAfterSessionStartDateAndHaveAcademicVacationAndByPayment(
            int studentGroupId, LocalDate sessionStartDate, Payment payment) {
        return studentExpelRepository.findCountStudentsInStudentGroupWhoExpelAfterSessionStartDateAndHaveAcademicVacationAndByPayment(
                studentGroupId, java.sql.Date.valueOf(sessionStartDate), payment.toString()
        );
    }

}

package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.RenewedAcademicVacationStudent;
import ua.edu.chdtu.deanoffice.entity.RenewedExpelledStudent;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.RenewedAcademicVacationStudentRepository;
import ua.edu.chdtu.deanoffice.repository.StudentAcademicVacationRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.util.StudentUtil;

import java.util.List;

@Service
public class StudentAcademicVacationService {
    private final StudentAcademicVacationRepository studentAcademicVacationRepository;
    private final StudentDegreeRepository studentDegreeRepository;
    private final RenewedAcademicVacationStudentRepository renewedAcademicVacationStudentRepository;
    private final StudentUtil studentUtil;

    @Autowired
    public StudentAcademicVacationService(
            StudentAcademicVacationRepository studentAcademicVacationRepository,
            StudentDegreeRepository studentDegreeRepository,
            RenewedAcademicVacationStudentRepository renewedAcademicVacationStudentRepository,
            StudentUtil studentUtil
    ) {
        this.studentAcademicVacationRepository = studentAcademicVacationRepository;
        this.studentDegreeRepository = studentDegreeRepository;
        this.renewedAcademicVacationStudentRepository = renewedAcademicVacationStudentRepository;
        this.studentUtil = studentUtil;
    }

    public StudentAcademicVacation giveAcademicVacation(StudentAcademicVacation studentAcademicVacation) {
        Integer id = studentAcademicVacation.getStudentDegree().getId();

        StudentDegree studentDegree = studentDegreeRepository.getOne(id);
        studentDegree.setActive(false);
        studentDegreeRepository.save(studentDegree);

        studentAcademicVacation.setActive(true);
        return studentAcademicVacationRepository.save(studentAcademicVacation);
    }

    public List<StudentAcademicVacation> getAll(Integer facultyId) {
        return studentAcademicVacationRepository.findAllActiveInFaculty(facultyId);
    }

    public List<StudentAcademicVacation> getActive(List<Integer> studentDegreeIds) {
        return studentAcademicVacationRepository.findActiveByStudentDegreeIds(studentDegreeIds);
    }

    @Transactional
    public RenewedAcademicVacationStudent renew(RenewedAcademicVacationStudent renewedAcademicVacationStudent) {
        Integer studentDegreeId = renewedAcademicVacationStudent.getStudentAcademicVacation().getStudentDegree().getId();
        studentUtil.studentDegreeToActive(studentDegreeId);
        updateStudentDegree(renewedAcademicVacationStudent);
        return renewedAcademicVacationStudentRepository.save(renewedAcademicVacationStudent);
    }

    private void updateStudentDegree(RenewedAcademicVacationStudent renewedAcademicVacationStudent) {
        StudentDegree studentDegree = renewedAcademicVacationStudent.getStudentAcademicVacation().getStudentDegree();

        studentDegree.setStudentGroup(renewedAcademicVacationStudent.getStudentGroup());
        studentDegree.setPayment(renewedAcademicVacationStudent.getPayment());

        studentDegreeRepository.save(studentDegree);
    }

    public List<StudentAcademicVacation> getByDegreeId(Integer studentDegreeId){
        return studentAcademicVacationRepository.findAllByDegreeId(studentDegreeId);
    }

    public StudentAcademicVacation getById(Integer studentAcademicVacationId) {
        return studentAcademicVacationRepository.getOne(studentAcademicVacationId);
    }

    public RenewedAcademicVacationStudent getRenewedByAcademicVacationId(int studentAcademicVacationId) {
        return renewedAcademicVacationStudentRepository.findByStudentAcademicVacationId(studentAcademicVacationId);
    }
}

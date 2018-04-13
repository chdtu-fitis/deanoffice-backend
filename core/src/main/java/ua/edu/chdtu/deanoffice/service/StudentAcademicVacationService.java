package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.RenewedAcademicVacationStudent;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.RenewedAcademicVacationStudentRepository;
import ua.edu.chdtu.deanoffice.repository.StudentAcademicVacationRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.util.List;

import static ua.edu.chdtu.deanoffice.util.StudentUtil.studentDegreeToActive;

@Service
public class StudentAcademicVacationService {
    private final StudentAcademicVacationRepository studentAcademicVacationRepository;
    private final StudentDegreeRepository studentDegreeRepository;
    private final RenewedAcademicVacationStudentRepository renewedAcademicVacationStudentRepository;

    @Autowired
    public StudentAcademicVacationService(
            StudentAcademicVacationRepository studentAcademicVacationRepository,
            StudentDegreeRepository studentDegreeRepository,
            RenewedAcademicVacationStudentRepository renewedAcademicVacationStudentRepository
    ) {
        this.studentAcademicVacationRepository = studentAcademicVacationRepository;
        this.studentDegreeRepository = studentDegreeRepository;
        this.renewedAcademicVacationStudentRepository = renewedAcademicVacationStudentRepository;
    }

    public StudentAcademicVacation giveAcademicVacation(StudentAcademicVacation studentAcademicVacation) {
        Integer id = studentAcademicVacation.getStudentDegree().getId();

        StudentDegree studentDegree = studentDegreeRepository.getOne(id);
        studentDegree.setActive(false);
        studentDegreeRepository.save(studentDegree);

        return studentAcademicVacationRepository.save(studentAcademicVacation);
    }

    public List<StudentAcademicVacation> getAll(Integer facultyId) {
        return studentAcademicVacationRepository.findAllByFaculty(facultyId);
    }

    public boolean inAcademicVacation(int studentAcademicVacationId) {
        StudentAcademicVacation studentAcademicVacation =
                studentAcademicVacationRepository.findActiveById(studentAcademicVacationId);
        return studentAcademicVacation != null;
    }

    public RenewedAcademicVacationStudent renew(RenewedAcademicVacationStudent renewedAcademicVacationStudent) {
        Integer studentDegreeId = renewedAcademicVacationStudent.getStudentAcademicVacation().getStudentDegree().getId();
        studentDegreeToActive(studentDegreeId, studentDegreeRepository);

        return renewedAcademicVacationStudentRepository.save(renewedAcademicVacationStudent);
    }

    public StudentAcademicVacation getById(Integer studentAcademicVacationId) {
        return studentAcademicVacationRepository.getOne(studentAcademicVacationId);
    }
}

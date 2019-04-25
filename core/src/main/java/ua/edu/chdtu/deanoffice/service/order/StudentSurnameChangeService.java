package ua.edu.chdtu.deanoffice.service.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentSurnameChange;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;
import ua.edu.chdtu.deanoffice.repository.StudentSurnameChangeRepository;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class StudentSurnameChangeService {
    private final StudentSurnameChangeRepository studentSurnameChangeRepository;
    private final CurrentYearRepository currentYearRepository;
    private final StudentRepository studentRepository;
    private final FacultyAuthorizationService facultyAuthorizationService;

    public StudentSurnameChangeService(StudentSurnameChangeRepository studentSurnameChangeRepository,
                                       CurrentYearRepository currentYearRepository,
                                       StudentRepository studentRepository, FacultyAuthorizationService facultyAuthorizationService) {
        this.studentSurnameChangeRepository = studentSurnameChangeRepository;
        this.currentYearRepository = currentYearRepository;
        this.studentRepository = studentRepository;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    public List<StudentSurnameChange> getAll() {
        return studentSurnameChangeRepository.findAll();
    }

    @Transactional
    public StudentSurnameChange saveStudentSurnameChange(ApplicationUser user,
                                                         StudentSurnameChange studentSurnameChange) throws
            OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        if (isNull(studentSurnameChange))
            throw new OperationCannotBePerformedException("");
        facultyAuthorizationService.verifyAccessibilityOfStudentDegrees(user, Collections.singletonList(studentSurnameChange.getStudentDegree()));

        studentSurnameChange.setSpecialityName(studentSurnameChange.getStudentDegree().getSpecialization().getSpeciality().getName());
        studentSurnameChange.setFacultyName(studentSurnameChange.getFaculty().getName());
        studentSurnameChange.setSpecializationName(studentSurnameChange.getStudentDegree().getSpecialization().getName());

        studentSurnameChange.setStudentYear(getStudentYear(studentSurnameChange.getStudentDegree()));

        studentSurnameChange.setStudentGroupName(studentSurnameChange.getStudentDegree().getStudentGroup().getName());
        studentSurnameChange.setTuitionForm(studentSurnameChange.getStudentDegree().getStudentGroup().getTuitionForm().getNameUkr());
        studentSurnameChange.setPayment(studentSurnameChange.getStudentDegree().getPayment().name());
        studentSurnameChange.setOldSurname(studentSurnameChange.getStudentDegree().getStudent().getSurname());

        Student student = studentSurnameChange.getStudentDegree().getStudent();
        student.setSurname(studentSurnameChange.getNewSurname());

        studentRepository.save(student);
        return studentSurnameChangeRepository.save(studentSurnameChange);
    }

    private int getStudentYear(StudentDegree studentDegree) {
        return getCurrentYear() -
                studentDegree.getStudentGroup().getCreationYear() +
                studentDegree.getStudentGroup().getBeginYears();
    }

    private int getCurrentYear() {
        return currentYearRepository.findOne(1).getCurrYear();
    }
}

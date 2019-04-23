package ua.edu.chdtu.deanoffice.service.order;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentSurnameChange;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.StudentSurnameChangeRepository;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class StudentSurnameChangeService {
    private final StudentSurnameChangeRepository studentSurnameChangeRepository;
    private final CurrentYearRepository currentYearRepository;

    public StudentSurnameChangeService(StudentSurnameChangeRepository studentSurnameChangeRepository,
                                       CurrentYearRepository currentYearRepository) {
        this.studentSurnameChangeRepository = studentSurnameChangeRepository;
        this.currentYearRepository = currentYearRepository;
    }

    public List<StudentSurnameChange> getAll() {
        return studentSurnameChangeRepository.findAll();
    }


    public StudentSurnameChange saveStudentSurnameChange(StudentSurnameChange studentSurnameChange) throws OperationCannotBePerformedException {
        if (isNull(studentSurnameChange))
            throw new OperationCannotBePerformedException("");

        studentSurnameChange.setSpecialityName(studentSurnameChange.getStudentDegree().getSpecialization().getSpeciality().getName());
        studentSurnameChange.setFacultyName(studentSurnameChange.getFaculty().getName());
        studentSurnameChange.setSpecializationName(studentSurnameChange.getStudentDegree().getSpecialization().getName());
        if (isActiveStudent(studentSurnameChange))
            studentSurnameChange.setStudentYear(getStudentYear(studentSurnameChange));

        studentSurnameChange.setStudentGroupName(studentSurnameChange.getStudentDegree().getStudentGroup().getName());
        studentSurnameChange.setTuitionForm(studentSurnameChange.getStudentDegree().getStudentGroup().getTuitionForm().getNameUkr());
        studentSurnameChange.setPayment(studentSurnameChange.getStudentDegree().getPayment().name());
        studentSurnameChange.setOldSurname(studentSurnameChange.getStudentDegree().getStudent().getSurname());

        return studentSurnameChangeRepository.save(studentSurnameChange);
    }

    private boolean isActiveStudent(StudentSurnameChange studentSurnameChange) {
        return studentSurnameChange.getStudentDegree().isActive();
    }

    private int getStudentYear(StudentSurnameChange studentSurnameChange) {
        return getCurrentYear() -
                studentSurnameChange.getStudentDegree().getStudentGroup().getCreationYear() +
                studentSurnameChange.getStudentDegree().getStudentGroup().getBeginYears();
    }

    private int getCurrentYear() {
        return currentYearRepository.findOne(1).getCurrYear();
    }
}

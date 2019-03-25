package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.StudentTransfer;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;
import ua.edu.chdtu.deanoffice.repository.StudentTransferRepository;

@Service
public class StudentTransferService {
    private final StudentTransferRepository studentTransferRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final CurrentYearRepository currentYearRepository;

    @Autowired
    public StudentTransferService (
            StudentTransferRepository studentTransferRepository,
            StudentGroupRepository studentGroupRepository,
            CurrentYearRepository currentYearRepository
    ){
        this.studentTransferRepository = studentTransferRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.currentYearRepository = currentYearRepository;
    }

    public StudentTransfer save(StudentTransfer studentTransfer){
        return studentTransferRepository.save(studentTransfer);
    }

    @Transactional
    public  void updateSpecializationAndStudentGroupAndPayment(Integer newSpecializationId, Integer newStudentGroupId, Payment newPayment, Integer studentDegreeId){
        studentTransferRepository.updateSpecializationAndStudentGroupAndPayment(newSpecializationId, newStudentGroupId, newPayment, studentDegreeId);
    }

    private int getCurrentYear() {
        return currentYearRepository.findOne(1).getCurrYear();
    }

    public Integer getStudyYear(Integer studentGroupId){
        StudentGroup studentGroup = studentGroupRepository.findOne(studentGroupId);
        Integer creationYear = studentGroup.getCreationYear();
        Integer beginYears = studentGroup.getBeginYears();
        Integer currYear = getCurrentYear();
        return currYear - creationYear + beginYears;
    }

    public Integer getSpecializationIdByStudentDegreeId(Integer studentDegreeId){
        return studentTransferRepository.getSpecializationIdByStudentDegreeId(studentDegreeId);
    }

    public Integer getStudentGroupIdByStudentDegreeId(Integer studentDegreeId){
        return studentTransferRepository.getStudentGroupIdByStudentDegreeId(studentDegreeId);
    }

    public Payment getPaymentByStudentDegreeId(Integer studentDegreeId){
        return studentTransferRepository.getPaymentByStudentDegreeId(studentDegreeId);
    }

}

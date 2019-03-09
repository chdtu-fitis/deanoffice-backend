package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.StudentTransfer;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;
import ua.edu.chdtu.deanoffice.repository.StudentTransferRepository;

import java.util.List;

@Service
public class DataVerificationService {
    private final StudentTransferRepository studentTransferRepository;
    private final StudentGroupRepository studentGroupRepository;

    @Autowired
    public DataVerificationService (
            StudentTransferRepository studentTransferRepository,
            StudentGroupRepository studentGroupRepository
    ){
        this.studentTransferRepository = studentTransferRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public void validateNewGroupExistsAndMatchesSpecialization(Integer specializationId, Integer studentGroupId) throws OperationCannotBePerformedException {
        List<StudentGroup> studentGroupToCheck = studentGroupRepository.findAllBySpecializationIdAndGroupId(specializationId, studentGroupId);
        if(studentGroupToCheck.size() == 0){
            String exceptionMessage = "Така група не існує на цій спеціальності!";
            throw new OperationCannotBePerformedException(exceptionMessage);
        }
    }

    public void validateTransferAfterSave(StudentTransfer studentTransferSaving) throws OperationCannotBePerformedException {
        if (studentTransferSaving == null) {
            throw new OperationCannotBePerformedException("Дані про переведення студента не вдалося зберегти");
        }
    }

}

package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithActiveEntity;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

@Service
public class DataVerificationService {

    private StudentDegreeRepository studentDegreeRepository;
    private final StudentGroupRepository studentGroupRepository;

    public DataVerificationService(StudentDegreeRepository studentDegreeRepository,
                                   StudentGroupRepository studentGroupRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public void isStudentDegreesActiveByIds(List<Integer> ids) throws OperationCannotBePerformedException {
        int countInactiveStudentDegrees = studentDegreeRepository.countInactiveStudentDegreesByIds(ids);
        if (countInactiveStudentDegrees != 0) {
            throw new OperationCannotBePerformedException("Серед даних студентів є неактивні");
        }
    }

    public void departmentNotNullAndActive(Department department,
                                           int departmentId) throws OperationCannotBePerformedException {
        if (department == null) {
            throw new OperationCannotBePerformedException("Кафедру [" + departmentId + "] не знайдено");
        }
        if (!department.isActive()) {
            throw new OperationCannotBePerformedException("Кафедра [" + departmentId + "] не активна в даний час");
        }
    }

    public void specializationNotNullAndActive(Specialization specialization,
                                               int specializationId) throws OperationCannotBePerformedException {
        if (specialization == null) {
            throw new OperationCannotBePerformedException("Освітню програму [" + specializationId + "] не знайдено");
        }
        if (!specialization.isActive()) {
            throw new OperationCannotBePerformedException("Освітня програма [" + specializationId + "] не активна в даний час");
        }
    }



    public void existActiveStudentDegreesInInactiveStudentGroups(List<StudentDegree> activeStudentDegrees) throws OperationCannotBePerformedException {
        for (StudentDegree activeStudentDegree : activeStudentDegrees) {
            if (activeStudentDegree.getStudentGroup().isActive() == false) {
                throw new OperationCannotBePerformedException("Активний студент не може входити в неактивну групу");
            }
        }
    }

    public void validateNewGroupExistsAndMatchesSpecialization(Integer specializationId, Integer studentGroupId) throws OperationCannotBePerformedException {
        List<StudentGroup> studentGroupToCheck = studentGroupRepository.findAllBySpecializationIdAndGroupId(specializationId, studentGroupId);
        if (studentGroupToCheck.size() == 0) {
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

package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.*;
import java.util.List;

@Service
public class DataVerificationService {

    private StudentDegreeRepository studentDegreeRepository;
    private final StudentGroupRepository studentGroupRepository;
    private CurrentYearService currentYearService;

    public DataVerificationService(StudentDegreeRepository studentDegreeRepository,
                                   StudentGroupRepository studentGroupRepository,
                                   CurrentYearService currentYearService) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.currentYearService = currentYearService;
    }

    public void isStudentDegreesActiveByIds(List<Integer> ids) throws OperationCannotBePerformedException {
        int countInactiveStudentDegrees = studentDegreeRepository.countInactiveStudentDegreesByIds(ids);
        if (countInactiveStudentDegrees != 0) {
            throw new OperationCannotBePerformedException("Серед даних студентів є неактивні");
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

    public void areStudentGroupsActiveByIds(List<Integer> ids) throws OperationCannotBePerformedException {
        int countInactiveStudentGroup = studentGroupRepository.countInactiveStudentGroupsByIds(ids);
        if (countInactiveStudentGroup != 0) {
            throw new OperationCannotBePerformedException("Серед даних груп є неактивні");
        }
    }

    public void specializationNotNull(Specialization specialization,
                                      int specializationId) throws OperationCannotBePerformedException {
        if (specialization == null) {
            throw new OperationCannotBePerformedException("Освітню програму [" + specializationId + "] не знайдено");
        }
    }

    public void specializationNotNullAndNotActive(Specialization specialization,
                                                  int specializationId) throws OperationCannotBePerformedException {
        specializationNotNull(specialization, specializationId);
        if (specialization.isActive()) {
            throw new OperationCannotBePerformedException("Освітня програма [" + specialization.getName() + "] активна в даний час");
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

    public void areGroupsGraduate(List<Integer> groupIds) throws OperationCannotBePerformedException {
        List<StudentGroup> studentGroups = studentGroupRepository.findAllByIds(groupIds);
        for (StudentGroup studentGroup : studentGroups) {
            if (currentYearService.getYear() != studentGroup.getCreationYear() + studentGroup.getStudyYears().intValue()) {
                throw new OperationCannotBePerformedException("Не всі дані групи є випускні");
            }
        }
    }
}

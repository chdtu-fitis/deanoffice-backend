package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.StudentTransfer;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

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

    public void areStudentGroupsActiveByIds(List<Integer> ids) throws OperationCannotBePerformedException {
        int countInactiveStudentGroup = studentGroupRepository.countInactiveStudentGroupsByIds(ids);
        if (countInactiveStudentGroup != 0) {
            throw new OperationCannotBePerformedException("Серед даних груп є неактивні");
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

    public void areGroupsGraduate(List<Integer> groupIds) throws OperationCannotBePerformedException {
        List<StudentGroup> studentGroups = studentGroupRepository.findAllByIds(groupIds);
        for (StudentGroup studentGroup : studentGroups) {
            if (currentYearService.getYear() != studentGroup.getCreationYear() + studentGroup.getStudyYears().intValue()) {
                throw new OperationCannotBePerformedException("Не всі дані групи є випускні");
            }
        }
    }

    public void isCorrectTeacher(Teacher teacher) throws OperationCannotBePerformedException {
        String errorMassage = null;
        if (teacher == null)
            errorMassage = "Не отримані дані для збереження!";

        if (teacher.getId() != 0)
            errorMassage = "Неправильно всказано id!";

        if (teacher.getName() == null)
            errorMassage = "Не вказано ім'я!";

        if (teacher.getSex() == null)
            errorMassage = "Не вказана стать!";

        if (teacher.getSurname() == null)
            errorMassage = "Не вказано прізвище!";

        if (teacher.getDepartment() == null)
            errorMassage = "Не вказана кафедра!";

        if (teacher.getDepartment().getId() == 0)
            errorMassage = "Вказана неіснуюча кафедра!";//Можливо зробити щоб була перевірка на всі неіснуючі кафедри, а не тільки на 0

        if (teacher.getPosition() == null)
            errorMassage = "Не сказана позиція!";

        if (teacher.getPosition().getId() == 0)
            errorMassage = "Вказана неіснуюча позиція!";

        if (errorMassage != null)
            throw new OperationCannotBePerformedException(errorMassage);
    }
}

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
    private DepartmentRepository departmentRepository;
    private PositionRepository positionRepository;
    private TeacherRepository teacherRepository;

    public DataVerificationService(StudentDegreeRepository studentDegreeRepository,
                                   StudentGroupRepository studentGroupRepository,
                                   CurrentYearService currentYearService,
                                   DepartmentRepository departmentRepository,
                                   PositionRepository positionRepository,
                                   TeacherRepository teacherRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.currentYearService = currentYearService;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.teacherRepository = teacherRepository;
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

    public void areTeachersActive(List<Teacher> teachers) throws OperationCannotBePerformedException {
        for (Teacher teacher: teachers) {
            if (teacher.isActive() == false)
                throw new OperationCannotBePerformedException("Серед даних вчителів є неактивні!");
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
        String errorMessage = null;
        errorMessage = (teacher.getName() == null) ? "Не вказано ім'я!" : errorMessage;
        errorMessage = (teacher.getSex() == null) ? "Не вказана стать!" : errorMessage;
        errorMessage = (teacher.getSurname() == null) ? "Не вказано прізвище!" : errorMessage;
        errorMessage = (teacher.getDepartment() == null) ? "Не вказана кафедра!" : errorMessage;
        errorMessage = (teacher.getDepartment().getId() == 0) ? "Вказана неіснуюча кафедра!" : errorMessage;
        errorMessage = (teacher.getPosition() == null) ? "Не вказана посада!" : errorMessage;
        errorMessage = (teacher.getPosition().getId() == 0) ? "Вказана неіснуюча посада!" : errorMessage;
        if (errorMessage != null)
            throw new OperationCannotBePerformedException(errorMessage);
    }
}

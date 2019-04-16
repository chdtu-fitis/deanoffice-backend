package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Position;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;

import java.util.List;

@Service
public class TeacherService {
    private TeacherRepository teacherRepository;
    private DataVerificationService dataVerificationService;
    private FacultyAuthorizationService facultyAuthorizationService;
    private DepartmentService departmentService;
    private PositionService positionService;

    public TeacherService(TeacherRepository teacherRepository, DataVerificationService dataVerificationService,
                          FacultyAuthorizationService facultyAuthorizationService, DepartmentService departmentService,
                          PositionService positionService) {
        this.teacherRepository = teacherRepository;
        this.dataVerificationService = dataVerificationService;
        this.facultyAuthorizationService = facultyAuthorizationService;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    public Teacher getTeacher(int teacherId) {
        return teacherRepository.findOne(teacherId);
    }

    public List<Teacher> getTeachers(List<Integer> ids) {
        return teacherRepository.findAll(ids);
    }

    public List<Teacher> getTeachersByActiveAndFacultyId(boolean active, int facultyId) {
        return teacherRepository.findAllByActiveAndFacultyId(active, facultyId);
    }

    public void deleteByIds(ApplicationUser user, List<Integer> ids) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        if (ids.size() == 0)
            throw new OperationCannotBePerformedException("Невказані ідентифікатори викладачів!");
        List<Teacher> teachers = getTeachers(ids);
        if (teachers.size() != ids.size())
            throw new OperationCannotBePerformedException("Серед даних ідентифікаторів викладачів є неіснуючі!");
        dataVerificationService.areTeachersActive(teachers);
        facultyAuthorizationService.verifyAccessibilityOfDepartments(user, teachers);
        teacherRepository.setTeachersInactiveByIds(ids);
    }

    public void saveTeacher(ApplicationUser user, Teacher teacher) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        dataVerificationService.isCorrectTeacher(teacher);
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacher.getDepartment());
        existDepartmentAndPositionInDataBase(teacher);
        teacherRepository.save(teacher);
    }

    public void updateTeacher(ApplicationUser user, Teacher teacher, Teacher teacherFromDB) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        dataVerificationService.isCorrectTeacher(teacher);
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacherFromDB.getDepartment());
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacher.getDepartment());
        existDepartmentAndPositionInDataBase(teacher);
        teacherRepository.save(teacher);
    }

    private void existDepartmentAndPositionInDataBase(Teacher teacher) throws OperationCannotBePerformedException {
        String errorMassage = null;
        Department department = departmentService.getById(teacher.getDepartment().getId());
        if (department == null)
            errorMassage = "Вказана неіснуюча кафедра!";
        Position position = positionService.getById(teacher.getPosition().getId());
        if (position == null)
            errorMassage = "Вказана неіснуюча посада!";
        if (errorMassage != null)
            throw new OperationCannotBePerformedException(errorMassage);
        setCorrectDepartmentAndPositionFromDataBase(teacher, department, position);
    }

    private void setCorrectDepartmentAndPositionFromDataBase(Teacher teacher, Department department, Position position) {
        teacher.setDepartment(department);
        teacher.setPosition(position);
    }
}

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
    @Autowired
    private TeacherRepository teacherRepository;
    private DataVerificationService dataVerificationService;
    private FacultyAuthorizationService facultyAuthorizationService;
    private DepartmentService departmentService;
    private PositionService positionService;

    public Teacher getTeacher(int teacherId) {
        return teacherRepository.findOne(teacherId);
    }



//    public List<Teacher> getTeachersByIds(List<Integer> ids) {
//        return teacherRepository.findAllByIds(ids);
//    }

    public List<Teacher> getTeachers(List<Integer> ids) {
        return teacherRepository.findAll(ids);
    }

    public List<Teacher> getTeachersByActive(boolean active) {
        return teacherRepository.findAllByActive(active);
    }

    public List<Teacher> getTeachersByActiveAndFacultyId(boolean active, int facultyId) {
        return teacherRepository.findAllByActiveAndFacultyId(active, facultyId);
    }

    public List<Teacher> getTeachersByActiveAndDepartmentId(boolean active, int departmentId) {
        return teacherRepository.findAllByActiveAndDepartmentId(active, departmentId);
    }

    public List<Teacher> getTeachersByActiveAndSurname(boolean active, String surname) {
        return teacherRepository.findAllByActiveAndSurname(active, surname);
    }

    public List<Teacher> getTeachersByActiveAndDepartmentIdAndSurname(boolean active, int departmentId, String surname) {
        return teacherRepository.findAllByActiveAndDepartmentIdAndSurname(active, departmentId, surname);
    }

    public void save(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    public void deleteByIds(List<Integer> ids) {
        teacherRepository.setTeachersInactiveByIds(ids);
    }

    public void saveTeacher(ApplicationUser user, Teacher teacher) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        dataVerificationService.isCorrectTeacher(teacher);
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacher.getDepartment());
        existDepartmentAndPositionInDataBase(teacher);
        this.save(teacher);
    }

    public void updateTeacher(ApplicationUser user, Teacher teacher, Teacher teacherFromDB) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        dataVerificationService.isCorrectTeacher(teacher);
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacherFromDB.getDepartment());
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacher.getDepartment());
        existDepartmentAndPositionInDataBase(teacher);
        this.save(teacher);
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

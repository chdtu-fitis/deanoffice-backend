package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
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

    public TeacherService(TeacherRepository teacherRepository, DataVerificationService dataVerificationService,
                          FacultyAuthorizationService facultyAuthorizationService) {
        this.teacherRepository = teacherRepository;
        this.dataVerificationService = dataVerificationService;
        this.facultyAuthorizationService = facultyAuthorizationService;
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

    public List<Teacher> getTeachersByActive(boolean active){
        return teacherRepository.findAllByActive(active);
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

    public Teacher saveTeacher(ApplicationUser user, Teacher teacher) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        dataVerificationService.isCorrectTeacher(teacher);
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacher.getDepartment());
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(ApplicationUser user, Teacher teacher, Teacher teacherFromDB) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        dataVerificationService.isCorrectTeacher(teacher);
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacherFromDB.getDepartment());
        facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacher.getDepartment());
        return teacherRepository.save(teacher);
    }
}

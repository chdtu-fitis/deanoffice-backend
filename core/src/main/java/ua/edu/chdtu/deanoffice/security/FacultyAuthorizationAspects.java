package ua.edu.chdtu.deanoffice.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.util.List;

@Component
@Aspect
public class FacultyAuthorizationAspects {
    private FacultyRepository facultyRepository;
    private final static String ACCESS_FORBIDDEN_FOR_USER = "Заборонено доступ для даного користувача!";

    @Autowired
    public FacultyAuthorizationAspects(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Before("within(ua.edu.chdtu.deanoffice.service" +
            ".DepartmentService) && @annotation(ua.edu.chdtu.deanoffice.security.FacultyAuthorized) && args(departmentId)")
    public void beforeGetDepartmentById(Integer departmentId) throws UnauthorizedFacultyDataException {
        int userFacultyId = FacultyUtil.getUserFacultyIdInt();
        int departmentFacultyId = facultyRepository.findIdByDepartment(departmentId);
        if (!(userFacultyId == departmentFacultyId))
            throw new UnauthorizedFacultyDataException(ACCESS_FORBIDDEN_FOR_USER + " Вибрана кафедра належить до іншого факультету");
    }

    @Before("within(ua.edu.chdtu.deanoffice.service.TeacherService) && @annotation(ua.edu.chdtu.deanoffice.security.FacultyAuthorized) && args(teacher)")
    public void beforeUpdateTeacher(Teacher teacher) throws UnauthorizedFacultyDataException {
        int userFacultyId = FacultyUtil.getUserFacultyIdInt();
        int teacherFacultyId = facultyRepository.findIdByTeacher(teacher.getId());
        if (!(userFacultyId == teacherFacultyId))
            throw new UnauthorizedFacultyDataException(ACCESS_FORBIDDEN_FOR_USER + " Вибраний викладач належить до іншого факультету");
    }

    @Before("within(ua.edu.chdtu.deanoffice.service" +
            ".TeacherService) && @annotation(ua.edu.chdtu.deanoffice.security.FacultyAuthorized) && args(teacherIds)")
    public void beforeRestoreOrDeleteTeacherById(List<Integer> teacherIds) throws UnauthorizedFacultyDataException {
        int userFacultyId = FacultyUtil.getUserFacultyIdInt();
        List<Integer> teacherFacultyIds = facultyRepository.findIdByTeachers(teacherIds);
        if (teacherFacultyIds.size() != teacherIds.size())
            throw new UnauthorizedFacultyDataException(ACCESS_FORBIDDEN_FOR_USER + " Вибраний викладач не існує");
        for(int teacherFacultyId: teacherFacultyIds) {
            if (teacherFacultyId != userFacultyId)
                throw new UnauthorizedFacultyDataException(ACCESS_FORBIDDEN_FOR_USER + " Вибраний викладач належить до іншого факультету");
        }
    }
}

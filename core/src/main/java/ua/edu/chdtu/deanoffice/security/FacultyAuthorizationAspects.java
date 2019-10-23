package ua.edu.chdtu.deanoffice.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

@Component
@Aspect
public class FacultyAuthorizationAspects {
    private FacultyRepository facultyRepository;
    private final static String ACCESS_FORBIDDEN_FOR_USER = "Заборонено доступ для даного користувача!";

    @Autowired
    public FacultyAuthorizationAspects(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Before("within(ua.edu.chdtu.deanoffice.service.DepartmentService) && @annotation(ua.edu.chdtu.deanoffice.security.FacultyAuthorized) && args(departmentId)")
    public void beforeGetDepartmentById(Integer departmentId) throws UnauthorizedFacultyDataException {
        String userFacultyId = FacultyUtil.getUserFacultyId();
        String departmentFacultyId = facultyRepository.findIdByDepartment(departmentId).toString();
        if (!userFacultyId.equals(departmentFacultyId))
            throw new UnauthorizedFacultyDataException(ACCESS_FORBIDDEN_FOR_USER + " Вибрана кафедра належить іншому факультету");
    }
}

package ua.edu.chdtu.deanoffice.service.security;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;

import java.util.List;

@Service
public class FacultyAuthorizationService {
    public void verifyAccessibilityOfGroupAndStudents(
            ApplicationUser user, List<StudentDegree> studentDegrees,
            StudentGroup studentGroup) throws UnauthorizedFacultyDataException
    {
        if (studentGroup.getSpecialization().getFaculty().getId() != user.getFaculty().getId()) {
            throw new UnauthorizedFacultyDataException("Передана група знаходиться у недоступному для користувача факультеті");
        }
        if (studentDegrees.stream().anyMatch(studentDegree -> studentDegree.getSpecialization().getFaculty().getId() != user.getFaculty().getId())) {
            throw new UnauthorizedFacultyDataException("Передані студенти знаходяться у недоступному для користувача факультеті");
        }
    }
}

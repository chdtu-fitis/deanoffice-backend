package ua.edu.chdtu.deanoffice.service.security;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;

import java.util.List;

@Service
public class FacultyAuthorizationService {
    public void verifyAccessibilityOfStudentGroup(ApplicationUser user, StudentGroup studentGroup) throws UnauthorizedFacultyDataException {
        if (user.getFaculty().getId() != studentGroup.getSpecialization().getFaculty().getId()) {
            throw new UnauthorizedFacultyDataException("Група знаходить в недоступному факультеті для поточного користувача");
        }
    }

    public void verifyAccessibilityOfGroupAndStudents(
            ApplicationUser user, List<StudentDegree> studentDegrees,
            StudentGroup studentGroup) throws UnauthorizedFacultyDataException
    {
        verifyAccessibilityOfStudentGroup(user, studentGroup);
        if (studentDegrees.stream().anyMatch(studentDegree -> studentDegree.getSpecialization().getFaculty().getId() != user.getFaculty().getId())) {
            throw new UnauthorizedFacultyDataException("Передані студенти знаходяться у недоступному для користувача факультеті");
        }
    }
}

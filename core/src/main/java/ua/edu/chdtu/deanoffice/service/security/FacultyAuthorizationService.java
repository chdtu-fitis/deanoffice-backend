package ua.edu.chdtu.deanoffice.service.security;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.util.List;

@Service
public class FacultyAuthorizationService {
    private final StudentDegreeRepository studentDegreeRepository;

    public FacultyAuthorizationService(StudentDegreeRepository studentDegreeRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
    }

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
        verifyAccessibilityOfStudentDegrees(user, studentDegrees);
    }

    public void verifyAccessibilityOfStudentDegrees(ApplicationUser user, List<StudentDegree> studentDegrees) throws UnauthorizedFacultyDataException {
        if (studentDegrees.stream().anyMatch(studentDegree -> studentDegree.getSpecialization().getFaculty().getId() != user.getFaculty().getId())) {
            throw new UnauthorizedFacultyDataException("Вибрані студенти навчаються на недоступному для користувача факультеті");
        }
    }

    public void verifyAccessibilityOfStudentDegrees(List<Integer> studentDegreeIds, ApplicationUser user) throws UnauthorizedFacultyDataException {
        List <Integer> studentDegreeIdFromDb = studentDegreeRepository.findIdsByIdsAndFacultyId(studentDegreeIds,user.getFaculty().getId());
        if (studentDegreeIdFromDb.size() != 0)
            throw new UnauthorizedFacultyDataException("Вибрані студенти навчаються на недоступному для користувача факультеті");

    }
}

package ua.edu.chdtu.deanoffice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

@Service
public class StudentUtil {
    private final StudentDegreeRepository studentDegreeRepository;
    private final CurrentYearRepository currentYearRepository;

    @Autowired
    public StudentUtil(
            StudentDegreeRepository studentDegreeRepository,
            CurrentYearRepository currentYearRepository
    ) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.currentYearRepository = currentYearRepository;
    }

    public void studentDegreeToActive(Integer studentDegreeId) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        studentDegree.setActive(true);
        studentDegreeRepository.save(studentDegree);
    }

    public int getStudyYear(StudentGroup studentGroup) {
        int currYear = this.currentYearRepository.getOne(1).getCurrYear();
        return currYear - studentGroup.getCreationYear();
    }

    public int getStudyYear(StudentDegree studentDegree) {
        return getStudyYear(studentDegree.getStudentGroup());
    }
}

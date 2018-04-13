package ua.edu.chdtu.deanoffice.util;

import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

public class StudentUtil {

    public static void studentDegreeToActive(Integer studentDegreeId, StudentDegreeRepository studentDegreeRepository) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        studentDegree.setActive(true);
        studentDegreeRepository.save(studentDegree);
    }
}

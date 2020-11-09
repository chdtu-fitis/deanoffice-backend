package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;

@Service
public class SelectiveCoursesStudentDegreesService {

    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;

    public SelectiveCoursesStudentDegreesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
    }

    public SelectiveCoursesStudentDegrees create(SelectiveCoursesStudentDegrees selectiveCoursesStudentDegrees) {
        return this.selectiveCoursesStudentDegreesRepository.save(selectiveCoursesStudentDegrees);
    }
}

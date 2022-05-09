package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IStudentsNotRightSelectiveCoursesNumber;

import java.util.List;

@Service
public class SelectiveCourseAnomaliesService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;

    public SelectiveCourseAnomaliesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
    }

    public List<IStudentsNotRightSelectiveCoursesNumber> getStudentsSelectedSelectiveCourses(int degreeId, int studyYear, int course, boolean more) {
        if (more) {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesOverNorm(degreeId, studyYear, course);
        }
        else {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesLessNorm(degreeId, studyYear, course);
        }

    }
}

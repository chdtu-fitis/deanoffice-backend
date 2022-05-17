package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IStudentsNotRightSelectiveCoursesNumber;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.AbnormalStudentsSelectiveCoursesSpecification;

import java.util.List;

@Service
public class SelectiveCourseAnomaliesService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;

    public SelectiveCourseAnomaliesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
    }

    public List<IStudentsNotRightSelectiveCoursesNumber> getStudentsSelectedSelectiveCourses(int degreeId, int studyYear, int studentYear, boolean more) {
        if (more) {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesOverNorm(degreeId, studyYear, studentYear);
        }
        else {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesLessNorm(AbnormalStudentsSelectiveCoursesSpecification.getSpecification());
        }

    }
}

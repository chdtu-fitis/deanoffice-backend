package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IStudentsNotRightSelectiveCoursesNumber;

import java.util.List;

@Service
public class SelectiveCourseAnomaliesService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private CurrentYearService currentYearService;

    public SelectiveCourseAnomaliesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository,
                                           CurrentYearService currentYearService) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.currentYearService = currentYearService;
    }

    public List<IStudentsNotRightSelectiveCoursesNumber> getStudentsSelectedSelectiveCourses(int degreeId, int studyYear, int studentYear, boolean moreNorm) {
        int currentYear = currentYearService.getYear();
        if (moreNorm) {
            return null; //selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesOverNorm(AbnormalStudentsSelectiveCoursesSpecification.getSpecification(degreeId, studyYear, studentYear,true));
        }
        else {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesLessNorm(degreeId, studyYear, studentYear, currentYear, -1L, 5L);
        }
    }
}

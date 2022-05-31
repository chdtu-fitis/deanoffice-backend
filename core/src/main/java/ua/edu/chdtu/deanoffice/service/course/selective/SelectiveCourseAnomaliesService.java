package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IStudentsNotRightSelectiveCoursesNumber;

import java.util.ArrayList;
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

    public List<IStudentsNotRightSelectiveCoursesNumber> getStudentsSelectedSelectiveCourses(int degreeId, int studyYear, Integer studentYear, boolean moreNorm) {
        int currentYear = currentYearService.getYear();
        List<Integer> studentYears = new ArrayList<>();
        if (studentYear == null) {
            for (int i = 1; i <= 4; i++)
                studentYears.add(i);
        } else {
            studentYears.add(studentYear);
        }
        if (moreNorm) {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesMoreOrLessNorm(degreeId, studyYear, studentYears, currentYear, 6L, 10L);
        } else {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesMoreOrLessNorm(degreeId, studyYear, studentYears, currentYear, 0L, 4L);
        }
    }
}

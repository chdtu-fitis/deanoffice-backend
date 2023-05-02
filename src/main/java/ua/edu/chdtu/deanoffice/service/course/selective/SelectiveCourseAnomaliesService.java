package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IStudentsNotRightSelectiveCoursesNumber;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Float.POSITIVE_INFINITY;

@Service
public class SelectiveCourseAnomaliesService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private CurrentYearService currentYearService;

    public SelectiveCourseAnomaliesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository,
                                           CurrentYearService currentYearService) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.currentYearService = currentYearService;
    }

    public List<IStudentsNotRightSelectiveCoursesNumber> getStudentsSelectedSelectiveCourses(int degreeId, int studyYear, Integer studentYear, boolean moreNorm) throws OperationCannotBePerformedException {
        int currentYear = currentYearService.getYear();
        List<Integer> studentYears = new ArrayList<>();
        int selectiveCoursesCount;
        if (studentYear == null) {
            for (Integer i : SelectiveCourseConstants.getSelectiveCourseChooseYears(degreeId, currentYear, studyYear))
                studentYears.add(i);
            selectiveCoursesCount = SelectiveCourseConstants.getSelectiveCoursesCount(degreeId);
        } else {
            studentYears.add(studentYear);
            selectiveCoursesCount = SelectiveCourseConstants.getSelectiveCoursesCount(degreeId, studentYear, currentYear, studyYear);
        }
        if (moreNorm) {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesMoreOrLessNorm(degreeId, studyYear, studentYears, currentYear,
                    selectiveCoursesCount + 1, (long) POSITIVE_INFINITY);
        } else {
            return selectiveCoursesStudentDegreesRepository.findStudentsSelectedSelectiveCoursesMoreOrLessNorm(degreeId, studyYear, studentYears, currentYear,
                    0L, selectiveCoursesCount - 1);
        }
    }
}

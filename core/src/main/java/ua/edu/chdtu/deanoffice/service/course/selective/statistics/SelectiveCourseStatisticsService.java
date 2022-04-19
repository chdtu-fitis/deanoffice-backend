package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import java.util.List;

@Service
public class SelectiveCourseStatisticsService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private CurrentYearService currentYearService;

    public SelectiveCourseStatisticsService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository, CurrentYearService currentYearService){
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.currentYearService = currentYearService;
    }

    public List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourse(int studyYear, int degreeId, SelectiveStatisticsCriteria selectiveStatisticsCriteria) {
        int currentYear = currentYearService.getYear();
        int i = 0;
        List<IPercentStudentsRegistrationOnCourses> registeredCounts;
        List<IPercentStudentsRegistrationOnCourses> allStudents;
        List<IPercentStudentsRegistrationOnCourses> registeredPercent;
        if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.YEAR) {
            registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByYear(studyYear, degreeId, currentYear);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsOnYears(degreeId, currentYear);
            registeredPercent = SelectiveCourseStatisticsServiceUtil.getStudentsPercentWhoChosenSelectiveCourseByYear(registeredCounts, allStudents);
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY) {
            registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId);
            registeredPercent = SelectiveCourseStatisticsServiceUtil.getStudentsPercentWhoChosenSelectiveCourseByАFaculty(registeredCounts, allStudents);
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.GROUP) {
            registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId);
            registeredPercent = SelectiveCourseStatisticsServiceUtil.getStudentsPercentWhoChosenSelectiveCourseByGroup(registeredCounts, allStudents);
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY_AND_SPECIALIZATION){
            registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(degreeId);
            registeredPercent = SelectiveCourseStatisticsServiceUtil.getStudentsPercentWhoChosenSelectiveCourseByFacultyAndSpecialization(registeredCounts, allStudents);
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY_AND_YEAR){
            registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(degreeId, currentYear);
            registeredPercent = SelectiveCourseStatisticsServiceUtil.getStudentsPercentWhoChosenSelectiveCourseByFacultyAndYear(registeredCounts, allStudents);
        }
        else {                          //FACULTY_AND_COURSES_AND_SPECIALIZATION
            registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(degreeId, currentYear);
            registeredPercent = SelectiveCourseStatisticsServiceUtil.getStudentsPercentWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(registeredCounts, allStudents);
        }
        return (registeredPercent);
    }
}

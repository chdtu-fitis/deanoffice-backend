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
        List<IPercentStudentsRegistrationOnCourses> registeredCounts;
        List<IPercentStudentsRegistrationOnCourses> allStudentsCounts;
        List<IPercentStudentsRegistrationOnCourses> registeredPercent;
        switch (selectiveStatisticsCriteria) {
            case YEAR:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByYear(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnYears(degreeId, currentYear);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getStudyYear() == registeredCounts.get(i).getStudyYear());
                break;
            case FACULTY:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()));
                break;
            case GROUP:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId, currentYear);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getGroupName().equals(registeredCounts.get(i).getGroupName()));
                break;
            case FACULTY_AND_SPECIALIZATION:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(degreeId);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getSpecializationName().equals(registeredCounts.get(i).getSpecializationName()) && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()));
                break;
            case FACULTY_AND_YEAR:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(degreeId, currentYear);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getStudyYear() == registeredCounts.get(i).getStudyYear()  && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()));
                break;
            default:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(degreeId, currentYear);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)->as.getStudyYear() == registeredCounts.get(i).getStudyYear()
                                            && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName())
                                            && as.getSpecializationName().equals(registeredCounts.get(i).getSpecializationName()));
        }
        return registeredPercent;
    }

    private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourse(
            List<IPercentStudentsRegistrationOnCourses> registeredCounts,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts,
            IStatisticsCondition statisticsCondition) {
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
            if (i == registeredCounts.size()) {
                as.setPercent(0);
                continue;
            }
            if (statisticsCondition.hasToBeCounted(as, registeredCounts,i)) {
                as.setPercent((int) (registeredCounts.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudentsCounts;
    }
}

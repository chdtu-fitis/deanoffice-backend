package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
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

    public List<StudentDegree> getStudentsNotSelectedSelectiveCourses(int degreeId, Integer studyYear) {
        return selectiveCoursesStudentDegreesRepository.findStudentsNotSelectedSelectiveCoursesByDegreeAndStudyYear(2022,1);
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
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourseByYear(registeredCounts, allStudentsCounts);
                break;
            case FACULTY:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourseByFaculty(registeredCounts, allStudentsCounts);
                break;
            case GROUP:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId, currentYear);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourseByGroup(registeredCounts, allStudentsCounts);
                break;
            case FACULTY_AND_SPECIALIZATION:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(degreeId);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourseByFacultyAndSpecialization(registeredCounts, allStudentsCounts);
                break;
            case FACULTY_AND_YEAR:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(degreeId, currentYear);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourseByFacultyAndYear(registeredCounts, allStudentsCounts);
                break;
            default:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(degreeId, currentYear);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(registeredCounts, allStudentsCounts);
        }
        return registeredPercent;
    }

    private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByYear(
            List<IPercentStudentsRegistrationOnCourses> registeredCounts,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts) {
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
            if (i >= registeredCounts.size()) {
                as.setPercent(0);
                continue;
            }
            if (as.getStudyYear() == registeredCounts.get(i).getStudyYear()) {
                as.setPercent((int) (registeredCounts.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudentsCounts;
    }

    private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByFaculty(
            List<IPercentStudentsRegistrationOnCourses> registeredCounts,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts) {
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
            if (i == registeredCounts.size()) {
                as.setPercent(0);
                continue;
            }
            if (as.getFacultyName().equals(registeredCounts.get(i).getFacultyName())) {
                as.setPercent((int) (registeredCounts.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudentsCounts;
    }

    private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByGroup(
            List<IPercentStudentsRegistrationOnCourses> registeredCounts,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts) {
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
            if (i == registeredCounts.size()) {
                as.setPercent(0);
                continue;
            }
            if (as.getGroupName().equals(registeredCounts.get(i).getGroupName())) {
                as.setPercent((int) (registeredCounts.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }

        return allStudentsCounts;
    }

    private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByFacultyAndSpecialization(
            List<IPercentStudentsRegistrationOnCourses> registeredCounts,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts) {
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
            if (i == registeredCounts.size()) {
                as.setPercent(0);
                continue;
            }
            if (as.getSpecializationName().equals(registeredCounts.get(i).getSpecializationName()) && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()) ) {
                as.setPercent((int) (registeredCounts.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudentsCounts;
    }

    private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByFacultyAndYear(
            List<IPercentStudentsRegistrationOnCourses> numberRegistered,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts) {
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
            if (i == numberRegistered.size()) {
                as.setPercent(0);
                continue;
            }
            if (as.getStudyYear() == numberRegistered.get(i).getStudyYear()  && as.getFacultyName().equals(numberRegistered.get(i).getFacultyName())) {
                as.setPercent((int) (numberRegistered.get(i).getCount() * 100 / as.getCount()));
                i++;
            }
            else {
                as.setPercent(0);
            }
        }
        return allStudentsCounts;
    }

    private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(
            List<IPercentStudentsRegistrationOnCourses> registeredCounts,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts) {
        int i = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
            if (i == registeredCounts.size()) {
                as.setPercent(0);
                continue;
            }
            if (as.getStudyYear() == registeredCounts.get(i).getStudyYear()
                    && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName())
                    && as.getSpecializationName().equals(registeredCounts.get(i).getSpecializationName())) {
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

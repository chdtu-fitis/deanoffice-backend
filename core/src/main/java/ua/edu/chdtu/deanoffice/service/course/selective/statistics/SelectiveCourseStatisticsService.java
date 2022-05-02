package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourseByYear(registeredCounts, allStudentsCounts);
                break;
            case FACULTY:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourseByАFaculty(registeredCounts, allStudentsCounts);
                break;
            case GROUP:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId);
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

    private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByАFaculty(
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

    public List<ICoursesSelectedByStudentsGroup> getCoursesSelectedByStudentsGroup(int studyYear, int groupId) {
        List<ICoursesSelectedByStudentsGroup> coursesSelectedByStudentsGroup = selectiveCoursesStudentDegreesRepository.findCoursesSelectedByStudentsGroup(studyYear, groupId);
        List<ICoursesSelectedByStudentsGroup> coursesSelectedByStudentsGroupFiltered =  new ArrayList<>();
        Map<String, Integer> registeredStudent;
        List<String> listNameCourses = new ArrayList<>();
        String courseName;
        int i = 0;
        for (ICoursesSelectedByStudentsGroup cs : coursesSelectedByStudentsGroup) {
            registeredStudent = new HashMap<>();
            courseName = cs.getCourseName();
            if (!(listNameCourses.contains(cs.getCourseName()))) {
                listNameCourses.add(courseName);
                coursesSelectedByStudentsGroupFiltered.add(cs);
                i++;
                for (ICoursesSelectedByStudentsGroup csbsg : coursesSelectedByStudentsGroup) {
                    if (courseName.equals(csbsg.getCourseName())){
                        registeredStudent.put(csbsg.getStudentFullName(), csbsg.getStudentDegreeId());
                    }
                }
                coursesSelectedByStudentsGroupFiltered.get(i - 1).setRegisteredStudent(registeredStudent);
            }
        }
        return coursesSelectedByStudentsGroupFiltered;
    }
}

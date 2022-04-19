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
        List<IPercentStudentsRegistrationOnCourses> numberRegistered;
        List<IPercentStudentsRegistrationOnCourses> allStudents;
        if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.YEAR) {
            numberRegistered = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByYear(studyYear, degreeId, currentYear);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsOnYears(degreeId,currentYear);
            for (IPercentStudentsRegistrationOnCourses as : allStudents) {
                if (i >= numberRegistered.size()) {
                    i--;
                    as.setPercent(0);
                }
                if (as.getStudyYear() == numberRegistered.get(i).getStudyYear()) {
                    as.setPercent((int) (numberRegistered.get(i).getPercent() * 100 / as.getPercent()));
                    i++;
                }
                else {
                    as.setPercent(0);
                }
            }
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY) {
            numberRegistered = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId);
            for (IPercentStudentsRegistrationOnCourses as : allStudents) {
                if (i == numberRegistered.size()) {
                    i--;
                    as.setPercent(0);
                }
                if (as.getFacultyName().equals(numberRegistered.get(i).getFacultyName())) {
                    as.setPercent((int) (numberRegistered.get(i).getPercent() * 100 / as.getPercent()));
                    i++;
                }
                else {
                    as.setPercent(0);
                }
            }
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.GROUP) {
            numberRegistered = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId);
            for (IPercentStudentsRegistrationOnCourses as : allStudents) {
                if (i == numberRegistered.size()) {
                    i--;
                    as.setPercent(0);
                }
                if (as.getGroupName().equals(numberRegistered.get(i).getGroupName())) {
                    as.setPercent((int) (numberRegistered.get(i).getPercent() * 100 / as.getPercent()));
                    i++;
                }
                else {
                    as.setPercent(0);
                }
            }
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY_AND_SPECIALIZATION){
            numberRegistered = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(degreeId);
            for (IPercentStudentsRegistrationOnCourses as : allStudents) {
                if (i == numberRegistered.size()) {
                    i--;
                    as.setPercent(0);
                }
                if (as.getSpecializationName().equals(numberRegistered.get(i).getSpecializationName()) && as.getFacultyName().equals(numberRegistered.get(i).getFacultyName()) ) {
                    as.setPercent((int) (numberRegistered.get(i).getPercent() * 100 / as.getPercent()));
                    i++;
                }
                else {
                    as.setPercent(0);
                }
            }
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY_AND_COURSES){
            numberRegistered = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndCourses(studyYear, degreeId, currentYear);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndCourses(degreeId, currentYear);
            for (IPercentStudentsRegistrationOnCourses as : allStudents) {
                if (i == numberRegistered.size()) {
                    i--;
                    as.setPercent(0);
                }
                if (as.getStudyYear() == numberRegistered.get(i).getStudyYear()  && as.getFacultyName().equals(numberRegistered.get(i).getFacultyName())) {
                    as.setPercent((int) (numberRegistered.get(i).getPercent() * 100 / as.getPercent()));
                    i++;
                }
                else {
                    as.setPercent(0);
                }
            }
        }
        else {                          //FACULTY_AND_COURSES_AND_SPECIALIZATION
            numberRegistered = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear);
            allStudents = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(degreeId, currentYear);
            for (IPercentStudentsRegistrationOnCourses as : allStudents) {
                if (i == numberRegistered.size()) {
                    i--;
                    as.setPercent(0);
                }
                if (as.getStudyYear() == numberRegistered.get(i).getStudyYear()
                        && as.getFacultyName().equals(numberRegistered.get(i).getFacultyName())
                        && as.getSpecializationName().equals(numberRegistered.get(i).getSpecializationName())) {
                    numberRegistered.get(i).setPercent((int) (numberRegistered.get(i).getPercent() * 100 / as.getPercent()));
                    i++;
                }
                else {
                    as.setPercent(0);
                }
            }
        }
        return (allStudents);
    }
}

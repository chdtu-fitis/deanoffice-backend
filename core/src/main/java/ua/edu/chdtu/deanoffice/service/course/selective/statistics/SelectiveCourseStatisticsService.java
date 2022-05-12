package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;

import java.util.ArrayList;
import java.util.List;

@Service
public class SelectiveCourseStatisticsService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private CurrentYearService currentYearService;

    public SelectiveCourseStatisticsService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository, CurrentYearService currentYearService){
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.currentYearService = currentYearService;
    }

    public List<StudentDegree> getStudentsNotSelectedSelectiveCourses(Integer studyYear, int degreeId) {
        return selectiveCoursesStudentDegreesRepository.findStudentsNotSelectedSelectiveCoursesByDegreeAndStudyYear(studyYear,degreeId);
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
                as.setRegisteredCount(0);
                continue;
            }
            if (statisticsCondition.hasToBeCounted(as, registeredCounts,i)) {
                as.setPercent((int) (registeredCounts.get(i).getRegisteredCount() * 100 / as.getTotalCount()));
                as.setRegisteredCount(registeredCounts.get(i).getRegisteredCount().intValue());
                i++;
            }
            else {
                as.setPercent(0);
                as.setRegisteredCount(0);
            }
        }
        return allStudentsCounts;
    }

    public List<CoursesSelectedByStudentsGroupResult> getCoursesSelectedByStudentGroup(int studyYear, int groupId) {
        List<ICoursesSelectedByStudentsGroup> coursesSelectedByStudentsGroup = selectiveCoursesStudentDegreesRepository.findCoursesSelectedByStudentsGroup(studyYear, groupId);
        List<CoursesSelectedByStudentsGroupResult> coursesSelectedByStudentsGroupFiltered = new ArrayList<>();
        List<StudentNameAndId> studentsNameAndId  =  new ArrayList<>();
        List<String> listNameCourses = new ArrayList<>();
        String courseName;
        for (ICoursesSelectedByStudentsGroup cs : coursesSelectedByStudentsGroup) {
            studentsNameAndId = new ArrayList<>();
            courseName = cs.getCourseName();
            if (!(listNameCourses.contains(cs.getCourseName()))) {
                listNameCourses.add(courseName);
                CoursesSelectedByStudentsGroupResult cssgResult = new CoursesSelectedByStudentsGroupResult(cs.getSelectiveCourseId(), cs.getStudentDegreeId(), cs.getSemester(),
                        cs.getCourseName(), cs.getTrainingCycle(), cs.getFieldOfKnowledgeCode());
                coursesSelectedByStudentsGroupFiltered.add(cssgResult);
                for (ICoursesSelectedByStudentsGroup csbsg : coursesSelectedByStudentsGroup) {
                    if (courseName.equals(csbsg.getCourseName())){
                        studentsNameAndId.add(new StudentNameAndId(csbsg.getStudentDegreeId(), csbsg.getStudentFullName()));
                    }
                }
                cssgResult.setStudents(studentsNameAndId);
            }
        }
        return coursesSelectedByStudentsGroupFiltered;
    }

    public List<AppointSelectiveCourse> appointSelectiveCourse(int studyYear) {
        List<IAppointSelectiveCourse> coursesSelectedByStudentsGroup = selectiveCoursesStudentDegreesRepository.findCoursesSelected(studyYear);
        List<AppointSelectiveCourse> coursesChosenByStudents = new ArrayList<>();
        List<String> coursesName;
        List<String> listStudentName = new ArrayList<>();
        String studentName;
        for (IAppointSelectiveCourse as : coursesSelectedByStudentsGroup) {
            coursesName = new ArrayList<>();
            studentName = as.getStudentName();
            if (!(listStudentName.contains(as.getStudentName()))) {
                listStudentName.add(studentName);
                AppointSelectiveCourse ascResult = new AppointSelectiveCourse(studentName);
                coursesChosenByStudents.add(ascResult);
                for (IAppointSelectiveCourse as2 : coursesSelectedByStudentsGroup) {
                    if (studentName.equals(as2.getStudentName())){
                        coursesName.add(as2.getCourseName());
                    }
                }
                ascResult.setCourseNam(coursesName);
            }
        }
        return coursesChosenByStudents;
    }
}

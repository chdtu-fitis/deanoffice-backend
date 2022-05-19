package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseConstants;

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
        int[] selectiveCoursesChooseYears = SelectiveCourseConstants.SELECTIVE_COURSES_CHOOSE_YEARS.get(degreeId);
        List<IPercentStudentsRegistrationOnCourses> registeredCounts;
        List<IPercentStudentsRegistrationOnCourses> allStudentsCounts;
        List<IPercentStudentsRegistrationOnCourses> registeredPercent;
        switch (selectiveStatisticsCriteria) {
            case YEAR:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByYear(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnYears(degreeId, currentYear, selectiveCoursesChooseYears);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getStudyYear() == registeredCounts.get(i).getStudyYear());
                break;
            case FACULTY:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId, currentYear, selectiveCoursesChooseYears);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()));
                break;
            case GROUP:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId, currentYear, selectiveCoursesChooseYears);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getGroupName().equals(registeredCounts.get(i).getGroupName()));
                break;
            case FACULTY_AND_SPECIALIZATION:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(degreeId, currentYear, selectiveCoursesChooseYears);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getSpecializationName().equals(registeredCounts.get(i).getSpecializationName()) && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()));
                break;
            case FACULTY_AND_YEAR:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(degreeId, currentYear, selectiveCoursesChooseYears);
                registeredPercent = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts,
                        (as, regCounts,i)-> as.getStudyYear() == registeredCounts.get(i).getStudyYear()  && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()));
                break;
            default:
                registeredCounts = selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear);
                allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(degreeId, currentYear, selectiveCoursesChooseYears);
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

    public GroupStudentsRegistrationResult getGroupStudentsRegistrationResult(int studyYear, int groupId) {
        List<ICoursesSelectedByStudentsGroup> coursesSelectedByStudentsGroup = selectiveCoursesStudentDegreesRepository.findCoursesSelectedByStudentsGroup(studyYear, groupId);
        List<CoursesSelectedByStudentsGroup> coursesSelectedByStudentsGroupFiltered = new ArrayList<>();
        List<StudentNameAndId> studentsNameAndId  =  new ArrayList<>();
        List<String> NameCourses = new ArrayList<>();
        List<Integer> RegisteredStudentsIds = new ArrayList<>();
        String courseName;
        for (ICoursesSelectedByStudentsGroup cs : coursesSelectedByStudentsGroup) {
            studentsNameAndId = new ArrayList<>();
            courseName = cs.getCourseName();
            if (!(NameCourses.contains(cs.getCourseName()))) {
                NameCourses.add(courseName);
                CoursesSelectedByStudentsGroup cssgResult = new CoursesSelectedByStudentsGroup(cs.getSelectiveCourseId(), cs.getStudentDegreeId(), cs.getSemester(),
                        cs.getCourseName(), cs.getTrainingCycle(), cs.getFieldOfKnowledgeCode());
                coursesSelectedByStudentsGroupFiltered.add(cssgResult);
                for (ICoursesSelectedByStudentsGroup csbsg : coursesSelectedByStudentsGroup) {
                    if (courseName.equals(csbsg.getCourseName())){
                        studentsNameAndId.add(new StudentNameAndId(csbsg.getStudentDegreeId(), csbsg.getStudentFullName()));
                        if (!RegisteredStudentsIds.contains(csbsg.getStudentDegreeId())){
                            RegisteredStudentsIds.add(csbsg.getStudentDegreeId());
                        }
                    }
                }
                cssgResult.setStudents(studentsNameAndId);
            }
        }
        List<ICoursesSelectedByStudentsGroup> notRegisteredStudents = selectiveCoursesStudentDegreesRepository.findNotRegisteredStudents(groupId, RegisteredStudentsIds);
        studentsNameAndId = new ArrayList<>();
        for (ICoursesSelectedByStudentsGroup nrs : notRegisteredStudents) {
            studentsNameAndId.add(new StudentNameAndId(nrs.getStudentDegreeId(), nrs.getStudentFullName()));
        }
        GroupStudentsRegistrationResult registeredStudentsNameResult = new GroupStudentsRegistrationResult(coursesSelectedByStudentsGroupFiltered,studentsNameAndId);
        return registeredStudentsNameResult;
    }
}

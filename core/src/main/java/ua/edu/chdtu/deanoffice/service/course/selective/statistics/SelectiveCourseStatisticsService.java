package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SelectiveCourseStatisticsService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private CurrentYearService currentYearService;

    public SelectiveCourseStatisticsService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository,
                                            CurrentYearService currentYearService) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.currentYearService = currentYearService;
    }

    public List<StudentDegree> getStudentsNotSelectedSelectiveCourses(Integer studyYear, int degreeId) {
        return selectiveCoursesStudentDegreesRepository.findStudentsNotSelectedSelectiveCoursesByDegreeAndStudyYear(studyYear, degreeId);
    }

    public List<PercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourse(
            int studyYear, int degreeId, SelectiveStatisticsCriteria selectiveStatisticsCriteria) throws OperationCannotBePerformedException {
        int currentYear = currentYearService.getYear();
        int[] selectiveCoursesChooseYears = SelectiveCourseConstants.getSelectiveCourseChooseYears(degreeId, currentYear, studyYear);
        List<List<IPercentStudentsRegistrationOnCourses>> registeredCounts = new ArrayList<>();
        List<IPercentStudentsRegistrationOnCourses> allStudentsCounts = null;
        List<List<IPercentStudentsRegistrationOnCourses>> choosingLessCounts = new ArrayList<>();
        for (int j : selectiveCoursesChooseYears) {
            int scNumberInTheYear = SelectiveCourseConstants.getSelectiveCoursesCount(degreeId, j, currentYear, studyYear);
            switch (selectiveStatisticsCriteria) {
                case YEAR:
                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByYear(studyYear, degreeId, currentYear, scNumberInTheYear, j).stream().collect(Collectors.toList()));
                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByYear(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnYears(degreeId, currentYear, selectiveCoursesChooseYears);
                    break;
                case FACULTY:
                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFaculty(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId, currentYear, selectiveCoursesChooseYears);
                    break;
                case GROUP:
                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByGroup(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId, currentYear, selectiveCoursesChooseYears);
                    break;
                case FACULTY_AND_SPECIALIZATION:
                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(degreeId, currentYear, selectiveCoursesChooseYears);
                    break;
                case FACULTY_AND_YEAR:
                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(degreeId, currentYear, selectiveCoursesChooseYears);
                    break;
                case FACULTY_AND_YEAR_AND_SPECIALIZATION:
                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(degreeId, currentYear, selectiveCoursesChooseYears);
                    break;
                default:
                    throw new OperationCannotBePerformedException("Неправильно вказаний критерій пошуку");
            }
            j++;
        }
        return getStudentsPercentWhoChosenSelectiveCourse(registeredCounts,
                choosingLessCounts, allStudentsCounts, selectiveStatisticsCriteria);
    }

    private Map<String, PercentStudentsRegistrationOnCourses> getSumMap(List<IPercentStudentsRegistrationOnCourses> lists,
                                                                        Map<String, PercentStudentsRegistrationOnCourses> sumMap,
                                                                        SelectiveStatisticsCriteria selectiveStatisticsCriteria) {
        List<IPercentStudentsRegistrationOnCourses> sumList = new ArrayList<>();
        String key;
        for (IPercentStudentsRegistrationOnCourses l : lists) {
            switch (selectiveStatisticsCriteria) {
                case YEAR:
                    key = String.valueOf(l.getStudyYear());
                    if(sumMap.get(key) == null) {
                        sumMap.put(key, new PercentStudentsRegistrationOnCourses(l.getStudyYear(), l.getCount()));
                    }
                    else {
                        sumMap.replace(l.getFacultyName(), new PercentStudentsRegistrationOnCourses(l.getStudyYear(),
                                sumMap.get(l.getFacultyName()).getCount() + l.getCount()));
                    }
                    break;
                case FACULTY:
                    if(sumMap.get(l.getFacultyName()) == null) {
                        sumMap.put(l.getFacultyName(), new PercentStudentsRegistrationOnCourses(l.getFacultyName(), l.getCount()));
                    }
                    else {
                        sumMap.replace(l.getFacultyName(), new PercentStudentsRegistrationOnCourses(l.getFacultyName(),
                                sumMap.get(l.getFacultyName()).getCount() + l.getCount()));
                    }
                    break;
                case GROUP:
                    if(sumMap.get(l.getGroupName()) == null) {
                        sumMap.put(l.getGroupName(), new PercentStudentsRegistrationOnCourses(l.getGroupName(),
                                l.getFacultyName(), l.getStudyYear(), l.getDepartment(),l.getCount()));
                    }
                    else {
                        sumMap.replace(l.getGroupName(), new PercentStudentsRegistrationOnCourses(l.getGroupName(),
                                l.getFacultyName(), l.getStudyYear(), l.getDepartment(),sumMap.get(l.getGroupName()).getCount() + l.getCount()));
                    }
                    break;
                case FACULTY_AND_SPECIALIZATION:
                    key = l.getFacultyName() + " " + l.getSpecializationName();
                    if(sumMap.get(key) == null) {
                        sumMap.put(key, new PercentStudentsRegistrationOnCourses(l.getFacultyName(),
                                l.getSpecializationName(),l.getCount()));
                    }
                    else {
                        sumMap.replace(key, new PercentStudentsRegistrationOnCourses(l.getFacultyName(),
                                l.getSpecializationName(),sumMap.get(key).getCount() + l.getCount()));
                    }
                    break;
                case FACULTY_AND_YEAR:
                    key = l.getFacultyName() + " " + l.getStudyYear();
                    if(sumMap.get(key) == null) {
                        sumMap.put(key, new PercentStudentsRegistrationOnCourses(l.getFacultyName(),
                                l.getStudyYear(),l.getCount()));
                    }
                    else {
                        sumMap.replace(key, new PercentStudentsRegistrationOnCourses(l.getFacultyName(),
                                    l.getStudyYear(),sumMap.get(key).getCount() + l.getCount()));
                    }
                    break;
                case FACULTY_AND_YEAR_AND_SPECIALIZATION:
                    key = l.getFacultyName() + " " + l.getStudyYear() + " " + l.getSpecializationName();
                    if(sumMap.get(key) == null) {
                        sumMap.put(key, new PercentStudentsRegistrationOnCourses(l.getFacultyName(),
                                l.getStudyYear(), l.getSpecializationName(), l.getCount()));
                    }
                    else {
                        sumMap.replace(key, new PercentStudentsRegistrationOnCourses(l.getFacultyName(),
                                l.getStudyYear(), l.getSpecializationName(), sumMap.get(key).getCount() + l.getCount()));
                    }
                    break;
            }
        }
        return sumMap;
    }

    private List<PercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourse (
            List<List<IPercentStudentsRegistrationOnCourses>> registeredCountsLists,
            List<List<IPercentStudentsRegistrationOnCourses>> choosingLessCountsLists,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCountsList,
            SelectiveStatisticsCriteria selectiveStatisticsCriteria) {
        Map<String, PercentStudentsRegistrationOnCourses> registeredCounts = new HashMap<>();
        Map<String, PercentStudentsRegistrationOnCourses> choosingLessCounts = new HashMap<>();
        Map<String, PercentStudentsRegistrationOnCourses> allStudentsCounts = new HashMap<>();
        allStudentsCounts = getSumMap(allStudentsCountsList, allStudentsCounts, selectiveStatisticsCriteria);
        for(List<IPercentStudentsRegistrationOnCourses> ls : registeredCountsLists) {
            registeredCounts = getSumMap(ls, registeredCounts, selectiveStatisticsCriteria);
        }
        for(List<IPercentStudentsRegistrationOnCourses> ls : choosingLessCountsLists) {
            choosingLessCounts = getSumMap(ls, choosingLessCounts, selectiveStatisticsCriteria);
        }
        return getJointStudentsPercent(allStudentsCounts, choosingLessCounts, registeredCounts);
    }

    private List<PercentStudentsRegistrationOnCourses> getJointStudentsPercent (Map<String, PercentStudentsRegistrationOnCourses> allStudentsCounts,
                                                                                Map<String, PercentStudentsRegistrationOnCourses> choosingLessCounts,
                                                                                Map<String, PercentStudentsRegistrationOnCourses> registeredCounts) {
        List<PercentStudentsRegistrationOnCourses> result = new ArrayList<>();
        int registeredCount;
        int choosingLessCount;
        for(Map.Entry<String, PercentStudentsRegistrationOnCourses> mp : allStudentsCounts.entrySet()) {
            registeredCount = 0;
            choosingLessCount = 0;
            if(registeredCounts.get(mp.getKey()) != null) registeredCount = registeredCounts.get(mp.getKey()).getCount();
            if(choosingLessCounts.get(mp.getKey()) != null ) choosingLessCount = choosingLessCounts.get(mp.getKey()).getCount();
            result.add(new PercentStudentsRegistrationOnCourses(mp.getValue().getGroupName(), mp.getValue().getFacultyName(),
                    mp.getValue().getStudyYear(), mp.getValue().getDepartment(), mp.getValue().getSpecializationName(), mp.getValue().getCount(),
                    registeredCount,choosingLessCount));
        }
        return result;
    }

    public GroupStudentsRegistrationResult getGroupStudentsRegistrationResult(int studyYear, int groupId) {
        List<ICoursesSelectedByStudentsGroup> coursesSelectedByStudentsGroup = selectiveCoursesStudentDegreesRepository.findCoursesSelectedByStudentsGroup(studyYear, groupId);
        List<CourseSelectedByStudentsGroup> coursesSelectedByStudentsGroupFiltered = new ArrayList<>();
        List<StudentNameAndId> students;
        List<String> courseNames = new ArrayList<>();
        Set<Integer> registeredStudentsIds = new HashSet<>();
        String courseName;
        for (ICoursesSelectedByStudentsGroup cs : coursesSelectedByStudentsGroup) {
            students = new ArrayList<>();
            courseName = cs.getCourseName();
            if (!(courseNames.contains(cs.getCourseName()))) {
                courseNames.add(courseName);
                CourseSelectedByStudentsGroup cssgResult = new CourseSelectedByStudentsGroup(cs.getSelectiveCourseId(), cs.getStudentDegreeId(), cs.getSemester(),
                        cs.getCourseName(), cs.getTrainingCycle(), cs.getFieldOfKnowledgeCode());
                coursesSelectedByStudentsGroupFiltered.add(cssgResult);
                for (ICoursesSelectedByStudentsGroup csbsg : coursesSelectedByStudentsGroup) {
                    if (courseName.equals(csbsg.getCourseName())) {
                        students.add(new StudentNameAndId(csbsg.getStudentDegreeId(), csbsg.getStudentFullName()));
                        registeredStudentsIds.add(csbsg.getStudentDegreeId());
                    }
                }
                cssgResult.setStudents(students);
            }
        }
        List<ICoursesSelectedByStudentsGroup> notRegisteredStudents = selectiveCoursesStudentDegreesRepository.findNotRegisteredStudents(groupId, new ArrayList(registeredStudentsIds));
        students = notRegisteredStudents.stream().map(nrs -> new StudentNameAndId(nrs.getStudentDegreeId(), nrs.getStudentFullName())).collect(Collectors.toList());
        GroupStudentsRegistrationResult registeredStudentsNameResult = new GroupStudentsRegistrationResult(coursesSelectedByStudentsGroupFiltered, students);
        return registeredStudentsNameResult;
    }
}

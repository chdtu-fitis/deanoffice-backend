package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SelectiveCourseStatisticsService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private CurrentYearService currentYearService;

    public SelectiveCourseStatisticsService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository, CurrentYearService currentYearService) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.currentYearService = currentYearService;
    }

    public List<StudentDegree> getStudentsNotSelectedSelectiveCourses(Integer studyYear, int degreeId) {
        return selectiveCoursesStudentDegreesRepository.findStudentsNotSelectedSelectiveCoursesByDegreeAndStudyYear(studyYear, degreeId);
    }

    public List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourse(int studyYear, int degreeId, SelectiveStatisticsCriteria selectiveStatisticsCriteria) {
        int currentYear = currentYearService.getYear();
        int[] selectiveCoursesChooseYears = SelectiveCourseConstants.SELECTIVE_COURSES_CHOOSE_YEARS.get(degreeId);
        List<List<IPercentStudentsRegistrationOnCourses>> registeredCounts = new ArrayList<>();
        List<IPercentStudentsRegistrationOnCourses> allStudentsCounts = null;
        List<List<IPercentStudentsRegistrationOnCourses>> choosingLessCounts = new ArrayList<>();
        int SelectiveCourseNumber;
        int j = 0;
        Map<String, Integer[]>[] SelectiveCourseNumbers = SelectiveCourseConstants.SELECTIVE_COURSES_NUMBER.get(degreeId);
        for (Map<String, Integer[]> scn : SelectiveCourseNumbers) {
            SelectiveCourseNumber = 0;
            if (scn != null) {
                for (Integer[] scn2 : scn.values()) {
                    for (Integer scn3 : scn2) {
                        SelectiveCourseNumber += scn3;
                        System.out.println(scn3);
                    }
                }
                switch (selectiveStatisticsCriteria) {
                    case YEAR:
                        registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByYear(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByYear(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnYears(degreeId, currentYear, selectiveCoursesChooseYears);
                        break;
                    case FACULTY:
                        registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFaculty(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId, currentYear, selectiveCoursesChooseYears);
                        break;
                    case GROUP:
                        registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByGroup(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId, currentYear, selectiveCoursesChooseYears);
                        break;
                    case FACULTY_AND_SPECIALIZATION:
                        registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(degreeId, currentYear, selectiveCoursesChooseYears);
                        break;
                    case FACULTY_AND_YEAR:
                        registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(degreeId, currentYear, selectiveCoursesChooseYears);
                        break;
                    default:
                        registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear, SelectiveCourseNumber,selectiveCoursesChooseYears[j]).stream().collect(Collectors.toList()));
                        allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(degreeId, currentYear, selectiveCoursesChooseYears);
                }
                j++;
            }
        }
        return getRegisteredPercents(selectiveStatisticsCriteria,getSumList(registeredCounts),getSumList(choosingLessCounts),allStudentsCounts);
    }
    private List<IPercentStudentsRegistrationOnCourses> getSumList (List<List<IPercentStudentsRegistrationOnCourses>> lists){
        List<IPercentStudentsRegistrationOnCourses> sumList = new ArrayList<>();
        boolean b;
        for(List<IPercentStudentsRegistrationOnCourses> ls : lists){
            if (sumList.size() == 0){
                sumList = ls;
            }
            else {
                for (IPercentStudentsRegistrationOnCourses l : ls){
                    b = true;
                    for (IPercentStudentsRegistrationOnCourses s : sumList) {
                        if (l.getFacultyName() != null && l.getSpecializationName() != null && l.getStudyYear() != null){
                            if (l.getSpecializationName().equals(s.getSpecializationName())
                                    && l.getFacultyName().equals(s.getFacultyName())
                                    && l.getStudyYear().equals(s.getStudyYear())) {
                                s.setCount((int) (s.getCount() + l.getCount()));
                                b = false;
                                break;
                            }
                        }
                        else if (l.getFacultyName() != null && l.getSpecializationName() != null){
                            if (l.getSpecializationName().equals(s.getSpecializationName())
                                    && l.getFacultyName().equals(s.getFacultyName())) {
                                s.setCount((int) (s.getCount() + l.getCount()));
                                b = false;
                                break;
                            }
                        }
                        else if (l.getFacultyName() != null && l.getStudyYear() != null){
                            if (l.getStudyYear().equals(s.getStudyYear())
                                && l.getFacultyName().equals(s.getFacultyName())){
                                s.setCount((int) (s.getCount() + l.getCount()));
                                b = false;
                                break;
                            }
                        }
                        else if (l.getGroupName() != null){
                            if(l.getGroupName().equals(s.getGroupName())) {
                                s.setCount((int) (s.getCount() + l.getCount()));
                                b = false;
                                break;
                            }
                        }
                        else if (l.getFacultyName() != null){
                            if (l.getFacultyName().equals(s.getFacultyName())) {
                                s.setCount((int) (s.getCount() + l.getCount()));
                                b = false;
                                break;
                            }
                        }
                        else if (l.getStudyYear() != null){
                            if(l.getStudyYear() == s.getStudyYear() ) {
                                s.setCount((int) (s.getCount() + l.getCount()));
                                b = false;
                                break;
                            }
                        }
                    }
                    if(b)sumList.add(l);
                }
            }
        }
        return sumList;
    }
    private List<IPercentStudentsRegistrationOnCourses> getRegisteredPercents (SelectiveStatisticsCriteria selectiveStatisticsCriteria,
                                                                               List<IPercentStudentsRegistrationOnCourses> registeredCounts,
                                                                               List<IPercentStudentsRegistrationOnCourses> choosingLessCounts,
                                                                               List<IPercentStudentsRegistrationOnCourses> allStudentsCounts) {
    List<IPercentStudentsRegistrationOnCourses> registeredPercents;
    switch(selectiveStatisticsCriteria)
    {
        case YEAR:
            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
                    (as, regCounts, i) -> as.getStudyYear() == choosingLessCounts.get(i).getStudyYear(),
                    (as, regCounts, i) -> as.getStudyYear() == registeredCounts.get(i).getStudyYear());
            break;
        case FACULTY:
            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
                    (as, regCounts, i) -> as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()),
                    (as, regCounts, i) -> as.getFacultyName().equals(choosingLessCounts.get(i).getFacultyName()));
            break;
        case GROUP:
            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
                    (as, regCounts, i) -> as.getGroupName().equals(registeredCounts.get(i).getGroupName()),
                    (as, regCounts, i) -> as.getGroupName().equals(choosingLessCounts.get(i).getGroupName()));
            break;
        case FACULTY_AND_SPECIALIZATION:
            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
                    (as, regCounts, i) -> as.getSpecializationName().equals(registeredCounts.get(i).getSpecializationName()) && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()),
                    (as, regCounts, i) -> as.getSpecializationName().equals(choosingLessCounts.get(i).getSpecializationName()) && as.getFacultyName().equals(choosingLessCounts.get(i).getFacultyName()));
            break;
        case FACULTY_AND_YEAR:
            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
                    (as, regCounts, i) -> as.getStudyYear() == registeredCounts.get(i).getStudyYear() && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName()),
                    (as, regCounts, i) -> as.getStudyYear() == choosingLessCounts.get(i).getStudyYear() && as.getFacultyName().equals(choosingLessCounts.get(i).getFacultyName()));
            break;
        default:
            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
                    (as, regCounts, i) -> as.getStudyYear() == registeredCounts.get(i).getStudyYear()
                            && as.getFacultyName().equals(registeredCounts.get(i).getFacultyName())
                            && as.getSpecializationName().equals(registeredCounts.get(i).getSpecializationName()),
                    (as, regCounts, i) -> as.getStudyYear() == choosingLessCounts.get(i).getStudyYear()
                            && as.getFacultyName().equals(choosingLessCounts.get(i).getFacultyName())
                            && as.getSpecializationName().equals(choosingLessCounts.get(i).getSpecializationName()));
    }
    return registeredPercents;
}

            private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourse (
            List<IPercentStudentsRegistrationOnCourses> registeredCounts,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts,
            List<IPercentStudentsRegistrationOnCourses> choosingLessCounts,
            IStatisticsCondition statisticsCondition,
            IStatisticsCondition statisticsCondition2) {
        int i = 0;
        int j = 0;
        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
             if (i == registeredCounts.size()) {
                as.setRegisteredPercent(0);
                as.setRegisteredCount(0);
                as.setChoosingLessPercent(0);
                as.setChoosingLessCount(0);
                as.setNotRegisteredPercent(0);
                as.setNotRegisteredCount(0);
                continue;
            }
            if (j < choosingLessCounts.size()  &&  statisticsCondition2.hasToBeCounted(as, choosingLessCounts,j)){
                as.setChoosingLessPercent((int) (choosingLessCounts.get(j).getCount() * 100 / as.getTotalCount()));
                as.setChoosingLessCount(choosingLessCounts.get(j).getCount().intValue());
                j++;
            }
            else {
                as.setChoosingLessPercent(0);
                as.setChoosingLessCount(0);
            }
            if (statisticsCondition.hasToBeCounted(as, registeredCounts,i)) {
                as.setRegisteredPercent((int) (registeredCounts.get(i).getCount() * 100 / as.getTotalCount()));
                as.setRegisteredCount(registeredCounts.get(i).getCount().intValue());
                as.setNotRegisteredCount((int) (as.getTotalCount() - as.getChoosingLessCount()- registeredCounts.get(i).getCount()));
                as.setNotRegisteredPercent((int) (as.getNotRegisteredCount() * 100 / as.getTotalCount()));
                i++;
            }
            else {
                as.setRegisteredPercent(0);
                as.setRegisteredCount(0);
                as.setNotRegisteredCount((int) (as.getTotalCount() - as.getChoosingLessCount()));
                as.setNotRegisteredPercent((int) (as.getNotRegisteredCount() * 100 / as.getTotalCount()));
            }
        }
        return allStudentsCounts;
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
                    if (courseName.equals(csbsg.getCourseName())){
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

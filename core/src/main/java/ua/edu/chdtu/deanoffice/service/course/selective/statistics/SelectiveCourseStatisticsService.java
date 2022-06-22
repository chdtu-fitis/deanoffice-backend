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

    //    public List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourse(
//            int studyYear, int degreeId, SelectiveStatisticsCriteria selectiveStatisticsCriteria) throws OperationCannotBePerformedException {
//        int currentYear = currentYearService.getYear();
//        int[] selectiveCoursesChooseYears = SelectiveCourseConstants.getSelectiveCourseChooseYears(degreeId, currentYear, studyYear);
//        List<List<IPercentStudentsRegistrationOnCourses>> registeredCounts = new ArrayList<>();
//        List<IPercentStudentsRegistrationOnCourses> allStudentsCounts = null;
//        List<List<IPercentStudentsRegistrationOnCourses>> choosingLessCounts = new ArrayList<>();
//        for (int j : selectiveCoursesChooseYears) {
//            int scNumberInTheYear = SelectiveCourseConstants.getSelectiveCoursesCount(degreeId, j, currentYear, studyYear);
//            switch (selectiveStatisticsCriteria) {
//                case YEAR:
//                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByYear(studyYear, degreeId, currentYear, scNumberInTheYear, j).stream().collect(Collectors.toList()));
//                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByYear(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnYears(degreeId, currentYear, selectiveCoursesChooseYears);
//                    break;
//                case FACULTY:
//                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFaculty(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFaculty(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnFaculty(degreeId, currentYear, selectiveCoursesChooseYears);
//                    break;
//                case GROUP:
//                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByGroup(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByGroup(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsOnGroup(degreeId, currentYear, selectiveCoursesChooseYears);
//                    break;
//                case FACULTY_AND_SPECIALIZATION:
//                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndSpecialization(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndSpecialization(degreeId, currentYear, selectiveCoursesChooseYears);
//                    break;
//                case FACULTY_AND_YEAR:
//                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndYear(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYear(degreeId, currentYear, selectiveCoursesChooseYears);
//                    break;
//                case FACULTY_AND_YEAR_AND_SPECIALIZATION:
//                    registeredCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    choosingLessCounts.add(selectiveCoursesStudentDegreesRepository.findStudentsRegisteredLessSelectiveCourseByFacultyAndYearAndSpecialization(studyYear, degreeId, currentYear, scNumberInTheYear,j).stream().collect(Collectors.toList()));
//                    allStudentsCounts = selectiveCoursesStudentDegreesRepository.findCountStudentsWhoChosenSelectiveCourseByFacultyAndYearAndSpecialization(degreeId, currentYear, selectiveCoursesChooseYears);
//                    break;
//                default:
//                    throw new OperationCannotBePerformedException("Неправильно вказаний критерій пошуку");
//            }
//            j++;
//        }
//        switch (selectiveStatisticsCriteria) {
//            case GROUP:
//                return getStudentsPercentWhoChosenSelectiveCourseByGroup(registeredCounts,
//                        choosingLessCounts, allStudentsCounts);
//        }
//        return getRegisteredPercents(selectiveStatisticsCriteria, getSumList(registeredCounts, selectiveStatisticsCriteria),
//                getSumList(choosingLessCounts, selectiveStatisticsCriteria), allStudentsCounts);
//
//    }
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
        switch (selectiveStatisticsCriteria) {
            case YEAR:
                break;
            case FACULTY:
                return getStudentsPercentWhoChosenSelectiveCourseByFaculty(registeredCounts,
                        choosingLessCounts, allStudentsCounts);
            case GROUP:
                return getStudentsPercentWhoChosenSelectiveCourseByGroup(registeredCounts,
                        choosingLessCounts, allStudentsCounts);
            case FACULTY_AND_SPECIALIZATION:
                break;
            case FACULTY_AND_YEAR:
                break;
            case FACULTY_AND_YEAR_AND_SPECIALIZATION:
                break;
        }
        return getStudentsPercentWhoChosenSelectiveCourseByGroup(registeredCounts,
                choosingLessCounts, allStudentsCounts);
    }


    private Map<String, PercentStudentsRegistrationOnCourses> getSumMapGroup(List<IPercentStudentsRegistrationOnCourses> lists,
                                                                             Map<String, PercentStudentsRegistrationOnCourses> sumMap) {
        List<IPercentStudentsRegistrationOnCourses> sumList = new ArrayList<>();
        for (IPercentStudentsRegistrationOnCourses l : lists) {
            if(sumMap.get(l.getGroupName()) == null){
                sumMap.put(l.getGroupName(), new PercentStudentsRegistrationOnCourses(
                        l.getFacultyName(), l.getStudyYear(), l.getDepartment(),l.getCount()));
            }
            else {
                sumMap.replace(l.getGroupName(), new PercentStudentsRegistrationOnCourses(
                        l.getFacultyName(), l.getStudyYear(), l.getDepartment(),sumMap.get(l.getGroupName()).getCount() + l.getCount()));
            }
        }
        return sumMap;
    }
    private Map<String, PercentStudentsRegistrationOnCourses> getSumMapFaculty(List<IPercentStudentsRegistrationOnCourses> lists,
                                                                               Map<String, PercentStudentsRegistrationOnCourses> sumMap) {
        List<IPercentStudentsRegistrationOnCourses> sumList = new ArrayList<>();
        for (IPercentStudentsRegistrationOnCourses l : lists) {
            if(sumMap.get(l.getFacultyName()) == null){
                sumMap.put(l.getFacultyName(), new PercentStudentsRegistrationOnCourses(l.getCount()));
            }
            else {
                sumMap.replace(l.getFacultyName(), new PercentStudentsRegistrationOnCourses(sumMap.get(l.getFacultyName()).getCount() + l.getCount()));
            }
        }
        return sumMap;
    }

    private List<PercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByGroup (
            List<List<IPercentStudentsRegistrationOnCourses>> registeredCountsLists,
            List<List<IPercentStudentsRegistrationOnCourses>> choosingLessCountsLists,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCountsList) {
        List<PercentStudentsRegistrationOnCourses> result = new ArrayList<>();
        Map<String, PercentStudentsRegistrationOnCourses> registeredCounts = new HashMap<>();
        Map<String, PercentStudentsRegistrationOnCourses> choosingLessCounts = new HashMap<>();
        Map<String, PercentStudentsRegistrationOnCourses> allStudentsCounts = new HashMap<>();
        allStudentsCounts = getSumMapGroup(allStudentsCountsList, allStudentsCounts);
        for(List<IPercentStudentsRegistrationOnCourses> ls : registeredCountsLists) {
            registeredCounts = getSumMapGroup(ls, registeredCounts);
        }
        for(List<IPercentStudentsRegistrationOnCourses> ls : choosingLessCountsLists) {
            choosingLessCounts = getSumMapGroup(ls, choosingLessCounts);
        }
        for(Map.Entry<String, PercentStudentsRegistrationOnCourses> mp : allStudentsCounts.entrySet()) {
            if(registeredCounts.get(mp.getKey()) == null && choosingLessCounts.get(mp.getKey()) == null) {
                result.add(new PercentStudentsRegistrationOnCourses(mp.getKey(), mp.getValue().getFacultyName(),
                        mp.getValue().getStudyYear(), mp.getValue().getDepartment(), mp.getValue().getCount(),
                        0,0));
            }
            else if(registeredCounts.get(mp.getKey()) != null && choosingLessCounts.get(mp.getKey()) == null) {
                result.add(new PercentStudentsRegistrationOnCourses(mp.getKey(), mp.getValue().getFacultyName(),
                        mp.getValue().getStudyYear(), mp.getValue().getDepartment(), mp.getValue().getCount(),
                        registeredCounts.get(mp.getKey()).getCount(),0));
            }
            else if(registeredCounts.get(mp.getKey()) == null && choosingLessCounts.get(mp.getKey()) != null) {
                result.add(new PercentStudentsRegistrationOnCourses(mp.getKey(), mp.getValue().getFacultyName(),
                        mp.getValue().getStudyYear(), mp.getValue().getDepartment(), mp.getValue().getCount(),
                        0,choosingLessCounts.get(mp.getKey()).getCount()));
            }
            else if(registeredCounts.get(mp.getKey()) != null && choosingLessCounts.get(mp.getKey()) != null) {
                result.add(new PercentStudentsRegistrationOnCourses(mp.getKey(), mp.getValue().getFacultyName(),
                        mp.getValue().getStudyYear(), mp.getValue().getDepartment(), mp.getValue().getCount(),
                        registeredCounts.get(mp.getKey()).getCount(), choosingLessCounts.get(mp.getKey()).getCount()));
            }
        }
        return result;
    }

    private List<PercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourseByFaculty (
            List<List<IPercentStudentsRegistrationOnCourses>> registeredCountsLists,
            List<List<IPercentStudentsRegistrationOnCourses>> choosingLessCountsLists,
            List<IPercentStudentsRegistrationOnCourses> allStudentsCountsList) {
        List<PercentStudentsRegistrationOnCourses> result = new ArrayList<>();
        Map<String, PercentStudentsRegistrationOnCourses> registeredCounts = new HashMap<>();
        Map<String, PercentStudentsRegistrationOnCourses> choosingLessCounts = new HashMap<>();
        Map<String, PercentStudentsRegistrationOnCourses> allStudentsCounts = new HashMap<>();
        allStudentsCounts = getSumMapFaculty(allStudentsCountsList, allStudentsCounts);
        for(List<IPercentStudentsRegistrationOnCourses> ls : registeredCountsLists) {
            registeredCounts = getSumMapFaculty(ls, registeredCounts);
        }
        for(List<IPercentStudentsRegistrationOnCourses> ls : choosingLessCountsLists) {
            choosingLessCounts = getSumMapFaculty(ls, choosingLessCounts);
        }
        for(Map.Entry<String, PercentStudentsRegistrationOnCourses> mp : allStudentsCounts.entrySet()) {
            if(registeredCounts.get(mp.getKey()) == null && choosingLessCounts.get(mp.getKey()) == null) {
                result.add(new PercentStudentsRegistrationOnCourses(mp.getKey(), mp.getValue().getCount(),
                        0,0));
            }
            else if(registeredCounts.get(mp.getKey()) != null && choosingLessCounts.get(mp.getKey()) == null) {
                result.add(new PercentStudentsRegistrationOnCourses(mp.getKey(), mp.getValue().getCount(),
                        registeredCounts.get(mp.getKey()).getCount(),0));
            }
            else if(registeredCounts.get(mp.getKey()) == null && choosingLessCounts.get(mp.getKey()) != null) {
                result.add(new PercentStudentsRegistrationOnCourses(mp.getKey(), mp.getValue().getCount(),
                        0,choosingLessCounts.get(mp.getKey()).getCount()));
            }
            else if(registeredCounts.get(mp.getKey()) != null && choosingLessCounts.get(mp.getKey()) != null) {
                result.add(new PercentStudentsRegistrationOnCourses(mp.getKey(), mp.getValue().getCount(),
                        registeredCounts.get(mp.getKey()).getCount(), choosingLessCounts.get(mp.getKey()).getCount()));
            }
        }
        return result;
    }

//        private List<IPercentStudentsRegistrationOnCourses> getSumList (List<List<IPercentStudentsRegistrationOnCourses>> lists,
//                                                                    SelectiveStatisticsCriteria selectiveStatisticsCriteria) {
//        List<IPercentStudentsRegistrationOnCourses> sumList = new ArrayList<>();
//        boolean b;
//        for(List<IPercentStudentsRegistrationOnCourses> ls : lists) {
//            if (sumList.size() == 0) {
//                sumList = ls;
//            }
//            else {
//                for (IPercentStudentsRegistrationOnCourses l : ls) {
//                    b = true;
//                    for (IPercentStudentsRegistrationOnCourses s : sumList) {
//                        switch (selectiveStatisticsCriteria) {
//                            case YEAR:
//                                if(l.getStudyYear() == s.getStudyYear() ) {
//                                    s.setCount((int) (s.getCount() + l.getCount()));
//                                    b = false;
//                                }
//                                break;
//                            case FACULTY:
//                                if (l.getFacultyName().equals(s.getFacultyName())) {
//                                    s.setCount((int) (s.getCount() + l.getCount()));
//                                    b = false;
//                                }
//                                break;
//                            case GROUP:
//                                if(l.getGroupName().equals(s.getGroupName())) {
//                                    s.setCount((int) (s.getCount() + l.getCount()));
//                                    b = false;
//                                }
//                                break;
//                            case FACULTY_AND_SPECIALIZATION:
//                                if (l.getSpecializationName().equals(s.getSpecializationName())
//                                        && l.getFacultyName().equals(s.getFacultyName())) {
//                                    s.setCount((int) (s.getCount() + l.getCount()));
//                                    b = false;
//                                }
//                                break;
//                            case FACULTY_AND_YEAR:
//                                if (l.getStudyYear().equals(s.getStudyYear())
//                                        && l.getFacultyName().equals(s.getFacultyName())) {
//                                    s.setCount((int) (s.getCount() + l.getCount()));
//                                    b = false;
//                                }
//                                break;
//                            case FACULTY_AND_YEAR_AND_SPECIALIZATION:
//                                if (l.getSpecializationName().equals(s.getSpecializationName())
//                                        && l.getFacultyName().equals(s.getFacultyName())
//                                        && l.getStudyYear().equals(s.getStudyYear())) {
//                                    s.setCount((int) (s.getCount() + l.getCount()));
//                                    b = false;
//                                }
//                                break;
//                            default:
//                                return null;
//                        }
//                    }
//                    if(b)sumList.add(l);
//                }
//            }
//        }
//        return sumList;
//    }
//    private List<IPercentStudentsRegistrationOnCourses> getRegisteredPercents (SelectiveStatisticsCriteria selectiveStatisticsCriteria,
//                                                                               List<IPercentStudentsRegistrationOnCourses> registeredCounts,
//                                                                               List<IPercentStudentsRegistrationOnCourses> choosingLessCounts,
//                                                                               List<IPercentStudentsRegistrationOnCourses> allStudentsCounts) {
//    List<IPercentStudentsRegistrationOnCourses> registeredPercents;
//    switch(selectiveStatisticsCriteria)
//    {
//        case YEAR:
//            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
//                    (as, regCounts) -> as.getStudyYear() == regCounts.getStudyYear(),
//                    (as, regCounts) -> as.getStudyYear() == regCounts.getStudyYear());
//            break;
//        case FACULTY:
//            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
//                    (as, regCounts) -> as.getFacultyName().equals(regCounts.getFacultyName()),
//                    (as, regCounts) -> as.getFacultyName().equals(regCounts.getFacultyName()));
//            break;
//        case GROUP:
//            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
//                    (as, regCounts) -> as.getGroupName().equals(regCounts.getGroupName()),
//                    (as, regCounts) -> as.getGroupName().equals(regCounts.getGroupName()));
//            break;
//        case FACULTY_AND_SPECIALIZATION:
//            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
//                    (as, regCounts) -> as.getSpecializationName().equals(regCounts.getSpecializationName()) && as.getFacultyName().equals(regCounts.getFacultyName()),
//                    (as, regCounts) -> as.getSpecializationName().equals(regCounts.getSpecializationName()) && as.getFacultyName().equals(regCounts.getFacultyName()));
//            break;
//        case FACULTY_AND_YEAR:
//            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
//                    (as, regCounts) -> as.getStudyYear() == regCounts.getStudyYear() && as.getFacultyName().equals(regCounts.getFacultyName()),
//                    (as, regCounts) -> as.getStudyYear() == regCounts.getStudyYear() && as.getFacultyName().equals(regCounts.getFacultyName()));
//            break;
//        case FACULTY_AND_YEAR_AND_SPECIALIZATION:
//            registeredPercents = getStudentsPercentWhoChosenSelectiveCourse(registeredCounts, allStudentsCounts, choosingLessCounts,
//                    (as, regCounts) -> as.getStudyYear() == regCounts.getStudyYear()
//                            && as.getFacultyName().equals(regCounts.getFacultyName())
//                            && as.getSpecializationName().equals(regCounts.getSpecializationName()),
//                    (as, regCounts) -> as.getStudyYear() == regCounts.getStudyYear()
//                            && as.getFacultyName().equals(regCounts.getFacultyName())
//                            && as.getSpecializationName().equals(regCounts.getSpecializationName()));
//            break;
//        default:
//            return null;
//    }
//    return registeredPercents;
//}
//
//            private List<IPercentStudentsRegistrationOnCourses> getStudentsPercentWhoChosenSelectiveCourse (
//            List<IPercentStudentsRegistrationOnCourses> registeredCounts,
//            List<IPercentStudentsRegistrationOnCourses> allStudentsCounts,
//            List<IPercentStudentsRegistrationOnCourses> choosingLessCounts,
//            IStatisticsCondition statisticsCondition,
//            IStatisticsCondition statisticsCondition2) {
//        boolean i;
//        boolean j;
//        for (IPercentStudentsRegistrationOnCourses as : allStudentsCounts) {
//            j = true;
//            i = true;
//            for (IPercentStudentsRegistrationOnCourses clc : choosingLessCounts) {
//                if (statisticsCondition2.hasToBeCounted(as, clc)) {
//                    as.setChoosingLessPercent((int) (clc.getCount() * 100 / as.getTotalCount()));
//                    as.setChoosingLessCount(clc.getCount().intValue());
//                    j = false;
//                }
//            }
//            if (j) {
//                as.setChoosingLessPercent(0);
//                as.setChoosingLessCount(0);
//            }
//            for (IPercentStudentsRegistrationOnCourses rc : registeredCounts) {
//                if (statisticsCondition.hasToBeCounted(as, rc)) {
//                    as.setRegisteredPercent((int) (rc.getCount() * 100 / as.getTotalCount()));
//                    as.setRegisteredCount(rc.getCount().intValue());
//                    as.setNotRegisteredCount((int) (as.getTotalCount() - as.getChoosingLessCount() - rc.getCount()));
//                    as.setNotRegisteredPercent((int) (as.getNotRegisteredCount() * 100 / as.getTotalCount()));
//                    i = false;
//                }
//            }
//            if (i) {
//                as.setRegisteredPercent(0);
//                as.setRegisteredCount(0);
//                as.setNotRegisteredCount((int) (as.getTotalCount() - as.getChoosingLessCount()));
//                as.setNotRegisteredPercent((int) (as.getNotRegisteredCount() * 100 / as.getTotalCount()));
//            }
//        }
//        return allStudentsCounts;
//    }

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

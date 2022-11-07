package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.DegreeEnum;
import ua.edu.chdtu.deanoffice.entity.TrainingCycle;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesYearParametersRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.util.DateUtil;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseConstants.SELECTIVE_COURSES_NUMBER;

@Service
public class SelectiveCoursesStudentDegreesService {

    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private SelectiveCoursesYearParametersRepository selectiveCoursesYearParametersRepository;
    private SelectiveCourseRepository selectiveCourseRepository;
    private CurrentYearService currentYearService;
    private StudentDegreeService studentDegreeService;
    private SelectiveCourseService selectiveCourseService;

    public SelectiveCoursesStudentDegreesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository,
                                                 SelectiveCoursesYearParametersRepository selectiveCoursesYearParametersRepository,
                                                 CurrentYearService currentYearService,
                                                 SelectiveCourseRepository selectiveCourseRepository,
                                                 SelectiveCourseService selectiveCourseService,
                                                 StudentDegreeService studentDegreeService) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.selectiveCoursesYearParametersRepository = selectiveCoursesYearParametersRepository;
        this.currentYearService = currentYearService;
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.studentDegreeService = studentDegreeService;
        this.selectiveCourseService = selectiveCourseService;
    }

    @Transactional
    public List<SelectiveCoursesStudentDegrees> create(List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees) {
        for (SelectiveCoursesStudentDegrees selectiveCourseStudentDegree : selectiveCoursesStudentDegrees)
            selectiveCourseStudentDegree.setActive(true);
        return this.selectiveCoursesStudentDegreesRepository.save(selectiveCoursesStudentDegrees);
    }

    public List<SelectiveCoursesStudentDegrees> getStudentDegreesForSelectiveCourse(int selectiveCourseId, boolean forFaculty) {
        if (forFaculty) {
            return selectiveCoursesStudentDegreesRepository.findActiveBySelectiveCourseAndFaculty(selectiveCourseId, FacultyUtil.getUserFacultyIdInt());
        } else {
            return selectiveCoursesStudentDegreesRepository.findActiveBySelectiveCourse(selectiveCourseId);
        }
    }

    public List<SelectiveCoursesStudentDegrees> getSelectiveCoursesByStudentDegreeIdAndSemester(int studentDegreeId, int semester) {
        return selectiveCoursesStudentDegreesRepository.findActiveByStudentDegreeAndSemester(studentDegreeId, semester);
    }

    public Map<SelectiveCourse, Long> getSelectiveCoursesWithStudentsCount(int studyYear, int semester, int degreeId, boolean all) {
        List<SelectiveCourse> selectiveCourses = selectiveCourseService.getSelectiveCoursesByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester, false, all);
        return calculateSelectiveCoursesStudentsCount(studyYear, semester, degreeId, selectiveCourses);
    }

    public Map<SelectiveCourse, Long> getSelectiveCoursesWithStudentsCountByStudentDegree(int studyYear, int semester, int degreeId, boolean all, int studentDegreeId)
            throws NotFoundException {
        List<SelectiveCourse> selectiveCourses = selectiveCourseService.getSelectiveCoursesByStudentDegree(studyYear, degreeId, semester, false, all, studentDegreeId);

        return calculateSelectiveCoursesStudentsCount(studyYear, semester, degreeId, selectiveCourses);
    }

    private Map<SelectiveCourse, Long> calculateSelectiveCoursesStudentsCount(int studyYear, int semester, int degreeId, List<SelectiveCourse> selectiveCourses) {
        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees = selectiveCoursesStudentDegreesRepository.findActiveByYearAndSemesterAndDegree(studyYear, semester, degreeId);
        Map<SelectiveCourse, Long> selectiveCoursesWithStudentsCount = selectiveCoursesStudentDegrees.stream()
                .collect(Collectors.groupingBy(SelectiveCoursesStudentDegrees::getSelectiveCourse, Collectors.counting()));

        Map<SelectiveCourse, Long> resultSelectiveCoursesWithStudentsCount = new LinkedHashMap<>();
        selectiveCourses.stream().forEach(selectiveCourse -> {
            Long count = selectiveCoursesWithStudentsCount.get(selectiveCourse);
            if (count != null) {
                resultSelectiveCoursesWithStudentsCount.put(selectiveCourse, count);
            } else {
                resultSelectiveCoursesWithStudentsCount.put(selectiveCourse, 0L);
            }
        });
        return resultSelectiveCoursesWithStudentsCount;
    }

    @Transactional
    public void disqualifySelectiveCoursesAndCancelStudentRegistrations(List<Integer> selectiveCourseIds) throws OperationCannotBePerformedException {
        int currentYear = currentYearService.getYear();
        List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersFromDB = selectiveCoursesYearParametersRepository.findAllByYear(currentYear);
        if (selectiveCoursesYearParametersFromDB == null)
            throw new OperationCannotBePerformedException("Параметри вибору вибіркових дисциплін за вказаним роком відсутні");

        SelectiveCoursesYearParameters selectiveCoursesYearParametersByDate = null;
        Date today = DateUtil.getTodayDate();

        for (SelectiveCoursesYearParameters selectiveCoursesYearParameters : selectiveCoursesYearParametersFromDB) {
            if (today.after(selectiveCoursesYearParameters.getFirstRoundEndDate()) && today.before(selectiveCoursesYearParameters.getSecondRoundStartDate()))
                selectiveCoursesYearParametersByDate = selectiveCoursesYearParameters;
        }

        if (selectiveCoursesYearParametersByDate == null)
            throw new OperationCannotBePerformedException("Не можна проводити дискваліфікацію в даний проміжок часу");

        selectiveCoursesStudentDegreesRepository.setSelectiveCoursesStudentDegreesInactiveBySelectiveCourseIds(selectiveCourseIds);
        selectiveCourseRepository.setSelectiveCoursesUnavailableByIds(selectiveCourseIds);
    }

    public SelectiveCoursesStudentDegreeId getSelectiveCoursesStudentDegreeIdByStudentDegreeId(boolean all, int studyYear, int studentDegreeId) {
        SelectiveCoursesStudentDegreeId selectiveCoursesForStudentDegreeId = new SelectiveCoursesStudentDegreeId();

        List<SelectiveCoursesStudentDegrees> selectiveCoursesForStudentDegree = selectiveCoursesStudentDegreesRepository
                .findAll(SelectiveCoursesStudentDegreeSpecification.getSelectiveCoursesStudentDegree(all, studyYear, Arrays.asList(studentDegreeId)));

        if (selectiveCoursesForStudentDegree.size() > 0) {
            selectiveCoursesForStudentDegree.forEach(elem -> elem.getSelectiveCourse().setAvailable(elem.isActive()));

            ExistingId existingId = new ExistingId();
            existingId.setId(selectiveCoursesForStudentDegree.get(0).getStudentDegree().getId());
            selectiveCoursesForStudentDegreeId.setStudentDegree(existingId);
            List<SelectiveCourse> selectiveCourseDTOs = selectiveCoursesForStudentDegree.stream().map(SelectiveCoursesStudentDegrees::getSelectiveCourse).collect(Collectors.toList());
            selectiveCoursesForStudentDegreeId.setSelectiveCourses(selectiveCourseDTOs);
        }

        return selectiveCoursesForStudentDegreeId;
    }

    public List<SelectiveCoursesStudentDegree> getSelectiveCoursesStudentDegreesByStudentDegreeIds(boolean all, int studyYear, List<Integer> studentDegreeIds) {
        List<SelectiveCoursesStudentDegree> selectiveCoursesForStudentDegrees = new ArrayList<>();

        if (studentDegreeIds.size() != 0) {
            Map<StudentDegree, List<SelectiveCoursesStudentDegrees>> selectiveCoursesStudentDegreesFromDB = selectiveCoursesStudentDegreesRepository
                    .findAll(SelectiveCoursesStudentDegreeSpecification.getSelectiveCoursesStudentDegree(all, studyYear, studentDegreeIds))
                    .stream()
                    .collect(Collectors.groupingBy(SelectiveCoursesStudentDegrees::getStudentDegree));

            for (Map.Entry<StudentDegree, List<SelectiveCoursesStudentDegrees>> entry : selectiveCoursesStudentDegreesFromDB.entrySet()) {
                List<SelectiveCourse> selectiveCourses = entry.getValue().stream().map(SelectiveCoursesStudentDegrees::getSelectiveCourse).collect(Collectors.toList());
                SelectiveCoursesStudentDegree selectiveCoursesStudentDegree = new SelectiveCoursesStudentDegree(entry.getKey(), selectiveCourses);
                selectiveCoursesForStudentDegrees.add(selectiveCoursesStudentDegree);
            }
        }

        return selectiveCoursesForStudentDegrees;
    }

    @Transactional
    public SelectiveCoursesStudentDegree substituteSelectiveCoursesForStudentDegree(int studyYear, int studentDegreeId,
                                                                                    List<Integer> selectiveCoursesIdsToAdd,
                                                                                    List<Integer> selectiveCoursesIdsToDrop) throws OperationCannotBePerformedException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);

        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegreesFromDB = selectiveCoursesStudentDegreesRepository
                .findAll(SelectiveCoursesStudentDegreeSpecification.getSelectiveCoursesStudentDegree(true, studyYear, Arrays.asList(studentDegreeId)));

        List<SelectiveCourse> selectiveCoursesToAdd = getAvailableSelectiveCoursesByStudyYearAndDegreeAndSemestersAndIds(studyYear, studentDegree, selectiveCoursesIdsToAdd);
        List<SelectiveCourse> selectiveCoursesToDrop = selectiveCoursesStudentDegreesFromDB.stream()
                .map(SelectiveCoursesStudentDegrees::getSelectiveCourse)
                .filter(selectiveCourse -> selectiveCoursesIdsToDrop.contains(selectiveCourse.getId()))
                .collect(Collectors.toList());

        if (selectiveCoursesToAdd.size() != selectiveCoursesToDrop.size())
            throw new OperationCannotBePerformedException("Кількість вибіркових дисциплін на відрахування не відповідає кількості на запис");

        List<Integer> semestersToAdd = getSelectiveCoursesSemesters(selectiveCoursesToAdd);
        List<Integer> semestersToDrop = getSelectiveCoursesSemesters(selectiveCoursesToDrop);
        if (!semestersToAdd.equals(semestersToDrop))
            throw new OperationCannotBePerformedException("Семестри вибіркових дисциплін на відрахування та запис не збігаються");

        List<Integer> selectiveCoursesToActivate = new ArrayList<>();
        for (SelectiveCoursesStudentDegrees selectiveCoursesForStudentDegree : selectiveCoursesStudentDegreesFromDB) {
            for (int selectiveCourseIdToAdd : selectiveCoursesIdsToAdd) {
                if (selectiveCoursesForStudentDegree.getSelectiveCourse().getId() == selectiveCourseIdToAdd) {
                    if (selectiveCoursesForStudentDegree.isActive())
                        throw new OperationCannotBePerformedException("Не можна записати студента на дисципліну, на яку він уже записаний");
                    selectiveCoursesToAdd.remove(selectiveCoursesForStudentDegree.getSelectiveCourse());
                    selectiveCoursesToActivate.add(selectiveCoursesForStudentDegree.getSelectiveCourse().getId());
                }
            }
        }

        for (SelectiveCoursesStudentDegrees selectiveCoursesForStudentDegree : selectiveCoursesStudentDegreesFromDB) {
            for (int selectiveCourseIdToDrop : selectiveCoursesIdsToDrop) {
                if (selectiveCoursesForStudentDegree.getSelectiveCourse().getId() == selectiveCourseIdToDrop && !selectiveCoursesForStudentDegree.isActive())
                    throw new OperationCannotBePerformedException("Не можна відрахувати студента з дисципліни, з якої він уже відрахований");
            }
        }

        selectiveCoursesStudentDegreesRepository.setSelectiveCoursesStudentDegreesStatusBySelectiveCourseIdsAndStudentDegreeIdAndStudyYear(studyYear, studentDegreeId, selectiveCoursesIdsToDrop, false);
        if (selectiveCoursesToActivate.size() != 0)
            selectiveCoursesStudentDegreesRepository.setSelectiveCoursesStudentDegreesStatusBySelectiveCourseIdsAndStudentDegreeIdAndStudyYear(studyYear, studentDegreeId, selectiveCoursesToActivate, true);

        List<SelectiveCourse> selectiveCoursesAfterSave = create(createSelectiveCoursesStudentDegreesList(selectiveCoursesToAdd, studentDegree)).stream()
                .map(SelectiveCoursesStudentDegrees::getSelectiveCourse).collect(Collectors.toList());

        return new SelectiveCoursesStudentDegree(studentDegree, selectiveCoursesAfterSave);
    }

    private List<Integer> getSelectiveCoursesSemesters(List<SelectiveCourse> selectiveCourses) {
        List<Integer> semesters = selectiveCourses.stream().map(selectiveCourse -> selectiveCourse.getCourse().getSemester()).sorted().collect(Collectors.toList());
        return semesters;
    }

//    public SelectiveCoursesStudentDegree enrollStudentInSelectiveCourses(int studyYear, int studentDegreeId, List<Integer> selectiveCourseIds)
//            throws OperationCannotBePerformedException {
//        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
//        List<SelectiveCourse> selectiveCourses = getAvailableSelectiveCoursesByStudyYearAndDegreeAndSemestersAndIds(studyYear, studentDegree, selectiveCourseIds);
//
//        List<SelectiveCourse> activeStudentDegreeSelectiveCoursesFromDB =
//                getSelectiveCoursesStudentDegreeIdByStudentDegreeId(false, studyYear, studentDegreeId).getSelectiveCourses();
//        if (activeStudentDegreeSelectiveCoursesFromDB == null)
//            activeStudentDegreeSelectiveCoursesFromDB = selectiveCourses;
//        else
//            activeStudentDegreeSelectiveCoursesFromDB.addAll(selectiveCourses);
//
//        if (!selectiveCourseService.checkSelectiveCoursesIntegrity(studentDegree, activeStudentDegreeSelectiveCoursesFromDB))
//            throw new OperationCannotBePerformedException("Кількість або семестри вибіркових предметів не відповідають правилам");
//
//        List<SelectiveCourse> selectiveCoursesAfterSave = create(createSelectiveCoursesStudentDegreesList(selectiveCourses, studentDegree)).stream()
//                .map(SelectiveCoursesStudentDegrees::getSelectiveCourse).collect(Collectors.toList());
//
//        return new SelectiveCoursesStudentDegree(studentDegree, selectiveCoursesAfterSave);
//    }

    public Map<SelectiveCourse, List<StudentDegree>> getStudentDegreesGroupedBySelectiveCourses(int studyYear, int semester, int degreeId) {
        List<SelectiveCoursesStudentDegrees> coursesForSemester = selectiveCoursesStudentDegreesRepository.findActiveByYearAndSemesterAndDegree(studyYear, semester, degreeId);
        Map<SelectiveCourse, List<SelectiveCoursesStudentDegrees>> selectiveCourseMap = coursesForSemester.stream()
                .collect(Collectors.groupingBy(SelectiveCoursesStudentDegrees::getSelectiveCourse));
        Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourse = new HashMap<>();
        selectiveCourseMap.keySet().forEach(selectiveCourse -> {
            List<StudentDegree> studentDegrees = selectiveCourseMap.get(selectiveCourse).stream()
                    .map(scsd -> scsd.getStudentDegree())
                    .collect(Collectors.toList());

            studentDegreesBySelectiveCourse.put(selectiveCourse, studentDegrees);
        });
        return studentDegreesBySelectiveCourse;
    }

    private List<SelectiveCourse> getAvailableSelectiveCoursesByStudyYearAndDegreeAndSemestersAndIds(
            int calendarYear, StudentDegree studentDegree, List<Integer> selectiveCourseIds) throws OperationCannotBePerformedException {
        if (studentDegree == null)
            throw new OperationCannotBePerformedException("Не існує student degree за таким id");

        int studentYear = studentDegreeService.getRealStudentDegreeYear(studentDegree, calendarYear);// + (nextYearSemesters ? 1 : 0);
        List<Integer> semesters = Arrays.asList(studentYear * 2 - 1, studentYear * 2);

        List<SelectiveCourse> selectiveCourses = selectiveCourseRepository.
                findAvailableByStudyYearAndDegreeAndSemestersAndIds(calendarYear, studentDegree.getSpecialization().getDegree().getId(), semesters, selectiveCourseIds);
        if (selectiveCourses.size() == 0)
            throw new OperationCannotBePerformedException("Для даного студента відсутні задані вибіркові дисципліни");

        return selectiveCourses;
    }

    private List<SelectiveCoursesStudentDegrees> createSelectiveCoursesStudentDegreesList(List<SelectiveCourse> selectiveCourses, StudentDegree studentDegree) {
        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegreesList = new ArrayList();

        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            SelectiveCoursesStudentDegrees selectiveCoursesStudentDegrees = new SelectiveCoursesStudentDegrees();
            selectiveCoursesStudentDegrees.setStudentDegree(studentDegree);
            selectiveCoursesStudentDegrees.setSelectiveCourse(selectiveCourse);
            selectiveCoursesStudentDegreesList.add(selectiveCoursesStudentDegrees);
        }

        return selectiveCoursesStudentDegreesList;
    }

    @Transactional
    public int registerMultipleStudentsForSelectiveCourses(List<StudentDegree> studentDegrees,
                                                            List<SelectiveCourse> selectiveCourses,
                                                            int registrationYear) {
        List<SelectiveCoursesStudentDegrees> scsdList = new ArrayList<>();
        int successful = 0;
        for (StudentDegree studentDegree : studentDegrees) {
            if (checkSelectiveCoursesIntegrityLoose(studentDegree, selectiveCourses, registrationYear)) {
                for (SelectiveCourse selectiveCourse : selectiveCourses) {
                    SelectiveCoursesStudentDegrees scsd = new SelectiveCoursesStudentDegrees(studentDegree, selectiveCourse);
                    scsdList.add(scsd);
                }
                successful++;
            }
        }
        this.selectiveCoursesStudentDegreesRepository.save(scsdList);
        return successful;
    }

/*checks for the year next to current. Призначено конкретно для запису на наступний навчальний рік черед мобільний
1.if the number of courses correspond the rules: number of GENERAL and PROFESSIONAl courses by semesters are equal to required;
2.if courses semesters correspond student year;
3. if all selective courses are for right registration year (usually, the next of the current study year)*/
    public boolean checkSelectiveCoursesIntegrityStrict(StudentDegree studentDegree, List<SelectiveCourse> selectiveCourses) {
        int studentDegreeYear = studentDegreeService.getRealStudentDegreeYear(studentDegree) + 1;
        int registrationYear = currentYearService.getYear() + 1;
        Set<SelectiveCourse> selectiveCoursesToCheck = buildSelectiveCoursesToCheckList(studentDegree, selectiveCourses, registrationYear);
        try {
            checkSemestersAndYearIntegrity(studentDegree, selectiveCoursesToCheck, studentDegreeYear, registrationYear);
            checkCoursesNumbersIntegrityEquals(studentDegree, selectiveCoursesToCheck, studentDegreeYear);
        } catch (InconsistentSelectiveCoursesToAddException e) {
            return false;
        }
        return true;
    }

/*checks
1.if the number of courses does not exceed the allowed with rules: number of GENERAL and PROFESSIONAl courses by semesters are less or equal to required;
2.if courses semesters correspond student year;
3.if all selective courses are for right registration year*/
    public boolean checkSelectiveCoursesIntegrityLoose(StudentDegree studentDegree, List<SelectiveCourse> selectiveCourses, int registrationYear) {
        int studentDegreeYear = studentDegreeService.getRealStudentDegreeYear(studentDegree, registrationYear);
        Set<SelectiveCourse> selectiveCoursesToCheck = buildSelectiveCoursesToCheckList(studentDegree, selectiveCourses, registrationYear);
        try {
            checkSemestersAndYearIntegrity(studentDegree, selectiveCoursesToCheck, studentDegreeYear, registrationYear);
            checkCoursesNumbersIntegrityLessOrEquals(studentDegree, selectiveCoursesToCheck, studentDegreeYear);
        } catch (InconsistentSelectiveCoursesToAddException e) {
            return false;
        }
        return true;
    }

    private Set<SelectiveCourse> buildSelectiveCoursesToCheckList(StudentDegree studentDegree, List<SelectiveCourse> selectiveCourses, int registrationYear) {
        List<SelectiveCourse> alreadyRegistered = selectiveCoursesStudentDegreesRepository
                .findActiveByStudentDegreeAndYear(studentDegree.getId(), registrationYear)
                .stream()
                .map(scsd -> scsd.getSelectiveCourse())
                .collect(Collectors.toList());
        selectiveCourses.removeAll(alreadyRegistered);
        Set<SelectiveCourse> selectiveCoursesToCheck = new HashSet<>();
        selectiveCoursesToCheck.addAll(alreadyRegistered);
        selectiveCoursesToCheck.addAll(selectiveCourses);
        return selectiveCoursesToCheck;
    }

    private void checkSemestersAndYearIntegrity(StudentDegree studentDegree, Set<SelectiveCourse> selectiveCoursesToCheck, int studentDegreeYear, int registrationYear) throws InconsistentSelectiveCoursesToAddException {
        for (SelectiveCourse selectiveCourse : selectiveCoursesToCheck) {
            if (selectiveCourse.getStudyYear() != registrationYear
                    || selectiveCourse.getDegree().getId() != studentDegree.getSpecialization().getDegree().getId())
                throw new InconsistentSelectiveCoursesToAddException();
            int semester = selectiveCourse.getCourse().getSemester();
            if (semester != studentDegreeYear * 2 - 1 && semester != studentDegreeYear * 2)
                throw new InconsistentSelectiveCoursesToAddException();
        }
    }

    private void checkCoursesNumbersIntegrityLessOrEquals(StudentDegree studentDegree, Set<SelectiveCourse> selectiveCourses, int studentDegreeYear) throws InconsistentSelectiveCoursesToAddException {
        CoursesNumbersByTrainingCycle cn = calculateCoursesNumberByTrainingCycle(selectiveCourses);
        Map<String, Integer[]> selCoursesNumbersByRule =
                SELECTIVE_COURSES_NUMBER.get(studentDegree.getSpecialization().getDegree().getId())[studentDegreeYear - 1];
        if (cn.getGeneral()[0] > selCoursesNumbersByRule.get(TrainingCycle.GENERAL.toString())[0]
                || cn.getGeneral()[1] > selCoursesNumbersByRule.get(TrainingCycle.GENERAL.toString())[1]
                || cn.getProfessional()[0] > selCoursesNumbersByRule.get(TrainingCycle.PROFESSIONAL.toString())[0]
                || cn.getProfessional()[1] > selCoursesNumbersByRule.get(TrainingCycle.PROFESSIONAL.toString())[1]) {
            throw new InconsistentSelectiveCoursesToAddException();
        }
    }

    private void checkCoursesNumbersIntegrityEquals(StudentDegree studentDegree, Set<SelectiveCourse> selectiveCourses, int studentDegreeYear) throws InconsistentSelectiveCoursesToAddException {
        CoursesNumbersByTrainingCycle cn = calculateCoursesNumberByTrainingCycle(selectiveCourses);
        Map<String, Integer[]> selCoursesNumbersByRule =
                SELECTIVE_COURSES_NUMBER.get(studentDegree.getSpecialization().getDegree().getId())[studentDegreeYear - 1];
        if (cn.getGeneral()[0] != selCoursesNumbersByRule.get(TrainingCycle.GENERAL.toString())[0]
                || cn.getGeneral()[1] != selCoursesNumbersByRule.get(TrainingCycle.GENERAL.toString())[1]
                || cn.getProfessional()[0] != selCoursesNumbersByRule.get(TrainingCycle.PROFESSIONAL.toString())[0]
                || cn.getProfessional()[1] != selCoursesNumbersByRule.get(TrainingCycle.PROFESSIONAL.toString())[1]) {
            throw new InconsistentSelectiveCoursesToAddException();
        }
    }

    private CoursesNumbersByTrainingCycle calculateCoursesNumberByTrainingCycle(Set<SelectiveCourse> selectiveCourses) throws InconsistentSelectiveCoursesToAddException {
        int general[] = {0, 0};
        int professional[] = {0, 0};
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            int semester = selectiveCourse.getCourse().getSemester();
            if (selectiveCourse.getTrainingCycle() == TrainingCycle.GENERAL)
                general[1 - semester % 2]++;
            if (selectiveCourse.getTrainingCycle() == TrainingCycle.PROFESSIONAL)
                professional[1 - semester % 2]++;
        }
        return new CoursesNumbersByTrainingCycle(general, professional);
    }

    private class CoursesNumbersByTrainingCycle {
        int general[];
        int professional[];

        public CoursesNumbersByTrainingCycle(int[] general, int[] professional) {
            this.general = general;
            this.professional = professional;
        }

        public int[] getGeneral() {
            return general;
        }

        public int[] getProfessional() {
            return professional;
        }
    }

    private class InconsistentSelectiveCoursesToAddException extends Exception {}
}

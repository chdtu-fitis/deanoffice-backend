package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.TypeCycle;
import ua.edu.chdtu.deanoffice.entity.DegreeEnum;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesYearParametersRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SelectiveCoursesStudentDegreesService {

    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private SelectiveCoursesYearParametersRepository selectiveCoursesYearParametersRepository;
    private SelectiveCourseRepository selectiveCourseRepository;
    private CurrentYearService currentYearService;
    private StudentDegreeService studentDegreeService;

    public SelectiveCoursesStudentDegreesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository,
                                                 SelectiveCoursesYearParametersRepository selectiveCoursesYearParametersRepository,
                                                 CurrentYearService currentYearService,
                                                 SelectiveCourseRepository selectiveCourseRepository,
                                                 StudentDegreeService studentDegreeService) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.selectiveCoursesYearParametersRepository = selectiveCoursesYearParametersRepository;
        this.currentYearService = currentYearService;
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.studentDegreeService = studentDegreeService;
    }

    @Transactional
    public List<SelectiveCoursesStudentDegrees> create(List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees) {
        for (SelectiveCoursesStudentDegrees selectiveCourseStudentDegree : selectiveCoursesStudentDegrees)
            selectiveCourseStudentDegree.setActive(true);
        return this.selectiveCoursesStudentDegreesRepository.save(selectiveCoursesStudentDegrees);
    }

    public List<SelectiveCoursesStudentDegrees> getStudentDegreesForSelectiveCourse(int selectiveCourseId, boolean forFaculty) {
        if (forFaculty) {
            return selectiveCoursesStudentDegreesRepository.findBySelectiveCourseAndFaculty(selectiveCourseId, FacultyUtil.getUserFacultyIdInt());
        } else {
            return selectiveCoursesStudentDegreesRepository.findBySelectiveCourse(selectiveCourseId);
        }
    }

    public List<SelectiveCoursesStudentDegrees> getSelectiveCoursesByStudentDegreeIdAndSemester(int studentDegreeId, int semester) {
        return selectiveCoursesStudentDegreesRepository.findByStudentDegreeAndSemester(studentDegreeId, semester);
    }

    public Map<SelectiveCourse, Long> getSelectiveCoursesWithStudentsCount(int studyYear, int semester, int degreeId) {
        List<SelectiveCourse> selectiveCourses = selectiveCourseRepository.findAllAvailableByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester);
        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees = selectiveCoursesStudentDegreesRepository.findActiveByYearAndSemesterAndDegree(studyYear, semester, degreeId);
        Map<SelectiveCourse, Long> selectiveCoursesWithStudentsCount = selectiveCoursesStudentDegrees.stream()
                .collect(Collectors.groupingBy(SelectiveCoursesStudentDegrees::getSelectiveCourse, Collectors.counting()));

        selectiveCourses.stream().forEach(selectiveCourse -> {
            if (!selectiveCoursesWithStudentsCount.keySet().contains(selectiveCourse))
                selectiveCoursesWithStudentsCount.put(selectiveCourse, 0L);
        });

        return selectiveCoursesWithStudentsCount;
    }

    @Transactional
    public void disqualifySelectiveCoursesAndCancelStudentRegistrations(int semester, int degreeId) throws OperationCannotBePerformedException {
        int currentYear = currentYearService.getYear();
        List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersFromDB = selectiveCoursesYearParametersRepository.findAllByYear(currentYear);
        if (selectiveCoursesYearParametersFromDB == null)
            throw new OperationCannotBePerformedException("Параметри вибору вибіркових дисциплін за вказаним роком відсутні");

        SelectiveCoursesYearParameters selectiveCoursesYearParametersByDate = null;
        Date today = new Date();

        for (SelectiveCoursesYearParameters selectiveCoursesYearParameters : selectiveCoursesYearParametersFromDB) {
            if (today.after(selectiveCoursesYearParameters.getFirstRoundEndDate()) && today.before(selectiveCoursesYearParameters.getSecondRoundStartDate()))
                selectiveCoursesYearParametersByDate = selectiveCoursesYearParameters;
        }

        if (selectiveCoursesYearParametersByDate == null)
            throw new OperationCannotBePerformedException("Не можна проводити дискваліфікацію в даний проміжок часу");

        Map<SelectiveCourse, Long> selectiveCoursesWithStudentsCount = getSelectiveCoursesWithStudentsCount(currentYear + 1, semester, degreeId);
        List<Integer> selectiveCourseIds = new ArrayList<>();

        Map<SelectiveCourse, Long> generalSelectiveCoursesWithStudentsCount = selectiveCoursesWithStudentsCount.entrySet().stream()
                .filter(selectiveCourseEntry -> selectiveCourseEntry.getKey().getTrainingCycle() == TypeCycle.GENERAL)
                .collect(Collectors.toMap(selectiveCourseEntry -> selectiveCourseEntry.getKey(), selectiveCourseEntry -> selectiveCourseEntry.getValue()));

        Map<SelectiveCourse, Long> professionalSelectiveCoursesWithStudentsCount = selectiveCoursesWithStudentsCount.entrySet().stream()
                .filter(selectiveCourseEntry -> selectiveCourseEntry.getKey().getTrainingCycle() == TypeCycle.PROFESSIONAL)
                .collect(Collectors.toMap(selectiveCourseEntry -> selectiveCourseEntry.getKey(), selectiveCourseEntry -> selectiveCourseEntry.getValue()));

        setGeneralSelectiveCourseIdsForRegistration(generalSelectiveCoursesWithStudentsCount, selectiveCoursesYearParametersByDate, degreeId, selectiveCourseIds);
        setProfessionalSelectiveCourseIdsForRegistration(professionalSelectiveCoursesWithStudentsCount, selectiveCoursesYearParametersByDate, degreeId, selectiveCourseIds);

        selectiveCoursesStudentDegreesRepository.setSelectiveCoursesStudentDegreesInactiveBySelectiveCourseIds(selectiveCourseIds);
        selectiveCourseRepository.setSelectiveCoursesUnavailableByIds(selectiveCourseIds);
    }

    private void setGeneralSelectiveCourseIdsForRegistration(Map<SelectiveCourse, Long> selectiveCoursesWithStudentsCount,
                                                             SelectiveCoursesYearParameters selectiveCoursesYearParameters,
                                                             int degreeId,
                                                             List<Integer> selectiveCourseIds) {

        for (Map.Entry<SelectiveCourse, Long> selectiveCourseWithStudentsCount : selectiveCoursesWithStudentsCount.entrySet()) {
            int count = selectiveCourseWithStudentsCount.getValue().intValue();

            if (degreeId == DegreeEnum.BACHELOR.getId() && count < selectiveCoursesYearParameters.getBachelorGeneralMinStudentsCount()) {
                selectiveCourseIds.add(selectiveCourseWithStudentsCount.getKey().getId());
            } else if (degreeId == DegreeEnum.MASTER.getId() && count < selectiveCoursesYearParameters.getMasterGeneralMinStudentsCount()) {
                selectiveCourseIds.add(selectiveCourseWithStudentsCount.getKey().getId());
            } else if (degreeId == DegreeEnum.PHD.getId() && count < selectiveCoursesYearParameters.getPhdGeneralMinStudentsCount()) {
                selectiveCourseIds.add(selectiveCourseWithStudentsCount.getKey().getId());
            }
        }
    }

    private void setProfessionalSelectiveCourseIdsForRegistration(Map<SelectiveCourse, Long> selectiveCoursesWithStudentsCount,
                                                                  SelectiveCoursesYearParameters selectiveCoursesYearParameters,
                                                                  int degreeId,
                                                                  List<Integer> selectiveCourseIds) {

        for(Map.Entry<SelectiveCourse, Long> selectiveCourseWithStudentsCount : selectiveCoursesWithStudentsCount.entrySet()) {
            int count = selectiveCourseWithStudentsCount.getValue().intValue();

            if (degreeId == DegreeEnum.BACHELOR.getId() && count < selectiveCoursesYearParameters.getBachelorProfessionalMinStudentsCount()) {
                selectiveCourseIds.add(selectiveCourseWithStudentsCount.getKey().getId());
            } else if (degreeId == DegreeEnum.MASTER.getId() && count < selectiveCoursesYearParameters.getMasterProfessionalMinStudentsCount()) {
                selectiveCourseIds.add(selectiveCourseWithStudentsCount.getKey().getId());
            } else if (degreeId == DegreeEnum.PHD.getId() && count < selectiveCoursesYearParameters.getPhdProfessionalMinStudentsCount()) {
                selectiveCourseIds.add(selectiveCourseWithStudentsCount.getKey().getId());
            }
        }
    }

    public SelectiveCoursesStudentDegreeId getSelectiveCoursesStudentDegreeIdByStudentDegreeId(boolean all, int studyYear, int studentDegreeId) {
        SelectiveCoursesStudentDegreeId selectiveCoursesForStudentDegreeId = new SelectiveCoursesStudentDegreeId();

        List<SelectiveCoursesStudentDegrees> selectiveCoursesForStudentDegree = selectiveCoursesStudentDegreesRepository
                .findAll(SelectiveCoursesStudentDegreeSpecification.getSelectiveCoursesStudentDegree(all, studyYear, Arrays.asList(studentDegreeId)));

        if (selectiveCoursesForStudentDegree.size() > 0) {
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
                SelectiveCoursesStudentDegree selectiveCoursesStudentDegree = new SelectiveCoursesStudentDegree();
                selectiveCoursesStudentDegree.setStudentDegree(entry.getKey());
                List<SelectiveCourse> selectiveCourses = entry.getValue().stream().map(SelectiveCoursesStudentDegrees::getSelectiveCourse).collect(Collectors.toList());
                selectiveCoursesStudentDegree.setSelectiveCourses(selectiveCourses);
                selectiveCoursesForStudentDegrees.add(selectiveCoursesStudentDegree);
            }
        }

        return selectiveCoursesForStudentDegrees;
    }

    @Transactional
    public void expelStudentDegreeFromSelectiveCourses(int studyYear, int studentDegreeId, List<Integer> selectiveCourseIds) {
        selectiveCoursesStudentDegreesRepository.setSelectiveCoursesStudentDegreesInactiveBySelectiveCourseIdsAndStudentDegreeIdAndStudyYear(studyYear, studentDegreeId, selectiveCourseIds);
    }

    public SelectiveCoursesStudentDegree enrollStudentInSelectiveCourses(int studyYear, int studentDegreeId, List<Integer> selectiveCourseIds) {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        List<SelectiveCourse> selectiveCourses = selectiveCourseRepository.findAllAvailableByStudyYearAndIds(studyYear, selectiveCourseIds);

        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegreesForSave = new ArrayList();

        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            SelectiveCoursesStudentDegrees selectiveCoursesStudentDegrees = new SelectiveCoursesStudentDegrees();
            selectiveCoursesStudentDegrees.setStudentDegree(studentDegree);
            selectiveCoursesStudentDegrees.setSelectiveCourse(selectiveCourse);
            selectiveCoursesStudentDegreesForSave.add(selectiveCoursesStudentDegrees);
        }

        List<SelectiveCourse> selectiveCoursesAfterSave = create(selectiveCoursesStudentDegreesForSave).stream()
                .map(SelectiveCoursesStudentDegrees::getSelectiveCourse).collect(Collectors.toList());

        SelectiveCoursesStudentDegree selectiveCoursesStudentDegree = new SelectiveCoursesStudentDegree();
        selectiveCoursesStudentDegree.setStudentDegree(studentDegree);
        selectiveCoursesStudentDegree.setSelectiveCourses(selectiveCoursesAfterSave);

        return selectiveCoursesStudentDegree;
    }
}

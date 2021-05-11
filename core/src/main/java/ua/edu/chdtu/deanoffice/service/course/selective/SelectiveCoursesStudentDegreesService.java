package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.entity.TypeCycle;
import ua.edu.chdtu.deanoffice.entity.DegreeEnum;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesYearParametersRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SelectiveCoursesStudentDegreesService {

    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private SelectiveCoursesYearParametersRepository selectiveCoursesYearParametersRepository;
    private SelectiveCourseRepository selectiveCourseRepository;
    private CurrentYearService currentYearService;

    public SelectiveCoursesStudentDegreesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository,
                                                 SelectiveCoursesYearParametersRepository selectiveCoursesYearParametersRepository,
                                                 CurrentYearService currentYearService,
                                                 SelectiveCourseRepository selectiveCourseRepository) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.selectiveCoursesYearParametersRepository = selectiveCoursesYearParametersRepository;
        this.currentYearService = currentYearService;
        this.selectiveCourseRepository = selectiveCourseRepository;
    }

    @Transactional
    public List<SelectiveCoursesStudentDegrees> create(List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees) {
        for (SelectiveCoursesStudentDegrees selectiveCourseStudentDegree : selectiveCoursesStudentDegrees)
            selectiveCourseStudentDegree.setActive(true);
        return this.selectiveCoursesStudentDegreesRepository.save(selectiveCoursesStudentDegrees);
    }

    public List<SelectiveCoursesStudentDegrees> getActiveSelectiveCoursesForStudentDegree(int studyYear, int studentDegreeId) {
        return selectiveCoursesStudentDegreesRepository.findAllAvailableByStudyYearAndStudentDegree(studyYear, studentDegreeId);
    }

    public List<SelectiveCoursesStudentDegrees> getAllSelectiveCoursesForStudentDegree(int studyYear, int studentDegreeId) {
        return selectiveCoursesStudentDegreesRepository.findAllByStudyYearAndStudentDegree(studyYear, studentDegreeId);
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
    public void disqualifySelectiveCoursesAndCancelStudentRegistrations(int semester, int degreeId) {
        int currentYear = currentYearService.getYear();
        Map<SelectiveCourse, Long> selectiveCoursesWithStudentsCount = getSelectiveCoursesWithStudentsCount(currentYear + 1, semester, degreeId);
        SelectiveCoursesYearParameters selectiveCoursesYearParameters = selectiveCoursesYearParametersRepository.findByYear(currentYear);
        List<Integer> selectiveCourseIds = new ArrayList<>();

        Map<SelectiveCourse, Long> generalSelectiveCoursesWithStudentsCount = selectiveCoursesWithStudentsCount.entrySet().stream()
                .filter(selectiveCourseEntry -> selectiveCourseEntry.getKey().getTrainingCycle() == TypeCycle.GENERAL)
                .collect(Collectors.toMap(selectiveCourseEntry -> selectiveCourseEntry.getKey(), selectiveCourseEntry -> selectiveCourseEntry.getValue()));

        Map<SelectiveCourse, Long> professionalSelectiveCoursesWithStudentsCount = selectiveCoursesWithStudentsCount.entrySet().stream()
                .filter(selectiveCourseEntry -> selectiveCourseEntry.getKey().getTrainingCycle() == TypeCycle.PROFESSIONAL)
                .collect(Collectors.toMap(selectiveCourseEntry -> selectiveCourseEntry.getKey(), selectiveCourseEntry -> selectiveCourseEntry.getValue()));

        setGeneralSelectiveCourseIdsForRegistration(generalSelectiveCoursesWithStudentsCount, selectiveCoursesYearParameters, degreeId, selectiveCourseIds);
        setProfessionalSelectiveCourseIdsForRegistration(professionalSelectiveCoursesWithStudentsCount, selectiveCoursesYearParameters, degreeId, selectiveCourseIds);

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
}

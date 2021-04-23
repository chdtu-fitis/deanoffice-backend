package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
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

    public List<SelectiveCoursesStudentDegrees> getSelectiveCoursesForStudentDegree(int studyYear, int studentDegreeId) {
        return selectiveCoursesStudentDegreesRepository.findAllAvailableByStudyYearAndStudentDegree(studyYear, studentDegreeId);
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
        Map<SelectiveCourse, Long> selectiveCoursesWithStudentsCount = getSelectiveCoursesWithStudentsCount(currentYear, semester, degreeId);
        SelectiveCoursesYearParameters selectiveCoursesYearParameters = selectiveCoursesYearParametersRepository.findByYear(currentYear);
        List<Integer> selectiveCourseIds = new ArrayList<>();

        for (Map.Entry<SelectiveCourse, Long> selectiveCourseWithStudentsCount : selectiveCoursesWithStudentsCount.entrySet()) {
            if (selectiveCourseWithStudentsCount.getValue().intValue() < selectiveCoursesYearParameters.getMinStudentsCount())
                selectiveCourseIds.add(selectiveCourseWithStudentsCount.getKey().getId());
        }

        selectiveCoursesStudentDegreesRepository.setSelectiveCoursesStudentDegreesInactiveBySelectiveCourseIds(selectiveCourseIds);
        selectiveCourseRepository.setSelectiveCoursesUnavailableByIds(selectiveCourseIds);
    }
}

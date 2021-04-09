package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SelectiveCoursesStudentDegreesService {

    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;

    public SelectiveCoursesStudentDegreesService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository) {
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
    }

    @Transactional
    public List<SelectiveCoursesStudentDegrees> create(List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees) {
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

    public Map<SelectiveCourse, Long> getSelectiveCoursesWithStudentsCount(int studyYear, int semester) {
        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees = selectiveCoursesStudentDegreesRepository.findByYearAndSemester(studyYear, semester);
        Map<SelectiveCourse, Long> selectiveCourseWithStudentsNumber = selectiveCoursesStudentDegrees.stream()
                .collect(Collectors.groupingBy(SelectiveCoursesStudentDegrees::getSelectiveCourse, Collectors.counting()));

        return selectiveCourseWithStudentsNumber;
    }
}

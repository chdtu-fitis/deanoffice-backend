package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SelectiveCourseService {
    private SelectiveCourseRepository selectiveCourseRepository;
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;
    private CurrentYearService currentYearService;
    private StudentDegreeService studentDegreeService;

    public SelectiveCourseService(SelectiveCourseRepository selectiveCourseRepository,
                                  SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository,
                                  CurrentYearService currentYearService,
                                  StudentDegreeService studentDegreeService) {
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
        this.currentYearService = currentYearService;
        this.studentDegreeService = studentDegreeService;
    }

    public List<SelectiveCourse> getSelectiveCoursesByStudyYearAndDegreeAndSemester(Integer studyYear, int degreeId, int semester, boolean thisYear, boolean all) {
        if (studyYear == null) {
            studyYear = thisYear ? currentYearService.getYear() : currentYearService.getYear() + 1;
        }
        if (all)
            return selectiveCourseRepository.findByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester);
        else
            return selectiveCourseRepository.findAvailableByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester);
    }

    public List<SelectiveCourse> getSelectiveCoursesByStudentDegree(Integer studyYear, int degreeId, int semester, boolean thisYear, boolean all, int studentDegreeId)
            throws NotFoundException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        if (studentDegree == null)
            throw new NotFoundException("Не існує student degree з таким id");

        if (studyYear == null) {
            studyYear = thisYear ? currentYearService.getYear() : currentYearService.getYear() + 1;
        }

        int fieldOfKnowledgeId = studentDegree.getSpecialization().getSpeciality().getFieldOfKnowledge().getId();
        if (all)
            return selectiveCourseRepository.findByStudyYearAndDegreeAndSemesterAndFk(studyYear, degreeId, semester, fieldOfKnowledgeId);
        else
            return selectiveCourseRepository.findAvailableByStudyYearAndDegreeAndSemesterAndFk(studyYear, degreeId, semester, fieldOfKnowledgeId);
    }

    public SelectiveCourse getById(Integer id) {
        return selectiveCourseRepository.findOne(id);
    }

    public List<SelectiveCourse> getSelectiveCourses(List<Integer> ids) {
        return selectiveCourseRepository.findAll(ids);
    }

    @Transactional
    public void delete(SelectiveCourse selectiveCourse) {
        selectiveCourse.setAvailable(false);
        List<SelectiveCoursesStudentDegrees> scsd = selectiveCoursesStudentDegreesRepository.findActiveBySelectiveCourse(selectiveCourse.getId());
        scsd.forEach(s -> {
            s.setActive(false);
            selectiveCoursesStudentDegreesRepository.save(s);
        });
        selectiveCourseRepository.save(selectiveCourse);
    }

    public SelectiveCourse create(SelectiveCourse selectiveCourse) {
        selectiveCourse.setAvailable(true);
        return this.selectiveCourseRepository.save(selectiveCourse);
    }

    public void restore(SelectiveCourse selectiveCourse) {
        selectiveCourse.setAvailable(true);
        selectiveCourseRepository.save(selectiveCourse);
    }

    public SelectiveCourse update(SelectiveCourse selectiveCourse) {
        return selectiveCourseRepository.save(selectiveCourse);
    }

    public PeriodCaseEnum getPeriodCaseByStudentDegree(StudentDegree studentDegree) {
        int studentDegreeYear = studentDegreeService.getRealStudentDegreeYear(studentDegree);

        if (studentDegreeYear < studentDegree.getStudentGroup().getRealBeginYear())
            return PeriodCaseEnum.LATE;
        else
            return PeriodCaseEnum.EARLY;
    }
}

package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.TypeCycle;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseConstants.SELECTIVE_COURSES_NUMBER;
import static ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseConstants.PERIOD_CASES;

@Service
public class SelectiveCourseService {
    private SelectiveCourseRepository selectiveCourseRepository;
    private CurrentYearService currentYearService;
    private StudentDegreeService studentDegreeService;

    public SelectiveCourseService(SelectiveCourseRepository selectiveCourseRepository, CurrentYearService currentYearService,
                                  StudentDegreeService studentDegreeService) {
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.currentYearService = currentYearService;
        this.studentDegreeService = studentDegreeService;
    }

    public List<SelectiveCourse> getSelectiveCoursesByStudyYearAndDegreeAndSemester(Integer studyYear, int degreeId, int semester, boolean thisYear) {
        if (studyYear == null) {
            studyYear = thisYear ? currentYearService.getYear() : currentYearService.getYear() + 1;
        }
        return selectiveCourseRepository.findAllAvailableByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester);
    }

    public SelectiveCourse getById(Integer id) {
        return selectiveCourseRepository.findOne(id);
    }

    public List<SelectiveCourse> getSelectiveCourses(List<Integer> ids) {
        return selectiveCourseRepository.findAll(ids);
    }

    public void delete(SelectiveCourse selectiveCourse) {
        selectiveCourse.setAvailable(false);
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

    /*checks 1.if the number of courses correspond the rules: number of GENERAL and PROFESSIONAl courses by semesters;
    2.if courses semesters correspond student year;
    3. if all selective courses are for right registration year (usually, the next of the current study year)*/
    public boolean checkSelectiveCoursesIntegrity(StudentDegree studentDegree, List<SelectiveCourse> selectiveCourses) {
        int studentDegreeYear = studentDegreeService.getStudentDegreeYear(studentDegree) + 1;
        Map<String, Integer[]> selCoursesNumbersByRule =
                SELECTIVE_COURSES_NUMBER.get(studentDegree.getSpecialization().getDegree().getId())[studentDegreeYear - 1];
        Integer general[] = {0, 0};
        Integer professional[] = {0, 0};
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            if (selectiveCourse.getStudyYear() != currentYearService.getYear() + 1
                    || selectiveCourse.getDegree().getId() != studentDegree.getSpecialization().getDegree().getId())
                return false;
            int semester = selectiveCourse.getCourse().getSemester();
            if (semester != studentDegreeYear * 2 - 1 && semester != studentDegreeYear * 2)
                return false;
            if (selectiveCourse.getTrainingCycle() == TypeCycle.GENERAL)
                general[1 - semester % 2]++;
            if (selectiveCourse.getTrainingCycle() == TypeCycle.PROFESSIONAL)
                professional[1 - semester % 2]++;
        }
        if (!Arrays.equals(general, selCoursesNumbersByRule.get(TypeCycle.GENERAL.toString()))
                || !Arrays.equals(professional, selCoursesNumbersByRule.get(TypeCycle.PROFESSIONAL.toString()))) {
            return false;
        }
        return true;
    }

    public PeriodCaseEnum getPeriodCaseByStudentDegree(StudentDegree studentDegree) {
        for (PeriodCase periodCase : PERIOD_CASES) {
            if ((studentDegree.getSpecialization().getDegree().getId() == periodCase.getDegreeId()
                    && studentDegreeService.getStudentDegreeYear(studentDegree) == periodCase.getYear())
                    && studentDegree.getTuitionTerm() == periodCase.getTuitionTerm())
                return periodCase.getPeriodCase();
        }

        return null;
    }
}

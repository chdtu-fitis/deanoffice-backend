package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.util.Arrays;
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

    /*===== Add selective courses group names to selective courses where group name is null=======*/
    @Transactional
    public void setGroupNames(int studentsYear, int degreeId) {
        List<SelectiveCourse> selectiveCoursesWithGroupNames = generateSelectiveCoursesGroupNamesSequential(studentsYear, degreeId);
        for (SelectiveCourse selectiveCourse: selectiveCoursesWithGroupNames)
            selectiveCourseRepository.updateGroupNameById(selectiveCourse.getGroupName(), selectiveCourse.getId());
    }

    /*For bachelor 2nd year, for instance*/
    private List<SelectiveCourse> generateSelectiveCoursesGroupNamesSequential(int studentsYear, int degreeId) {
        int studyYear = currentYearService.getYear() + 1;

        Integer[] semesters = {studentsYear * 2 - 1, studentsYear * 2};
        List<SelectiveCourse> selectiveCourses = selectiveCourseRepository
                .findAvailableByStudyYearAndDegreeAndSemesters(studyYear, degreeId, Arrays.asList(semesters));

        String studyYearLastTwoDigits = String.valueOf(studyYear).substring(2);

        int sequenceNumber = 1;
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            StringBuilder groupName = new StringBuilder();

//            int semester = selectiveCourse.getCourse().getSemester();
//            int year = (int) Math.ceil((double)semester / 2); /* курс, на якому вивчається дисципліна */

//            char typeCycleFirstLetter = selectiveCourse.getTrainingCycle() == TypeCycle.GENERAL ? 'з' : 'п';

            groupName
                    .append(selectiveCourse.getDegree().getName().substring(0, 1))
                    .append(studyYearLastTwoDigits)
                    .append("-")
                    .append(studentsYear)
                    .append("к")
                    .append("-")
                    .append(sequenceNumber++);

            selectiveCourse.setGroupName(groupName.toString());
            if (sequenceNumber > 3) break;
        }

        return selectiveCourses;
    }

    /*For bachelor 3rd year, for instance, where all group studies the same selective courses*/
    private List<SelectiveCourse> generateSelectiveCoursesGroupNamesGrouped() {
        return null;
    }
    //==============================
}

package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.TrainingCycle;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SelectiveCourseService {
    private final String[] SPECIAL_FIELDS_OF_KNOWLEDGE = {"07", "12"};

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
    public void setGroupNames(int studentsYear, int degreeId) throws OperationCannotBePerformedException {
        Set<String> specialFieldsOfKnowledge = new HashSet<>(Arrays.asList(SPECIAL_FIELDS_OF_KNOWLEDGE));
        List<SelectiveCourse> selectiveCoursesWithGroupNames = generateSelectiveCoursesGroupNamesGeneral(studentsYear, degreeId, specialFieldsOfKnowledge);
        for (SelectiveCourse selectiveCourse: selectiveCoursesWithGroupNames)
            selectiveCourseRepository.updateGroupNameById(selectiveCourse.getGroupName(), selectiveCourse.getId());
    }

    private List<SelectiveCourse> generateSelectiveCoursesGroupNamesGeneral(int studentsYear, int degreeId, Set<String> specialFieldsOfKnowledge)
            throws OperationCannotBePerformedException {
        int studyYear = currentYearService.getYear() + 1;

        Integer[] semesters = {studentsYear * 2 - 1, studentsYear * 2};
        List<SelectiveCourse> selectiveCourses = selectiveCourseRepository
                .findAvailableByStudyYearAndDegreeAndSemesters(studyYear, degreeId, Arrays.asList(semesters));
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            if (selectiveCourse.getGroupName() != null) {
                String errorMessage = "Не можна автоматично генерувати назви груп, якщо серед вибіркових предметів є хоча б один з призначеною назвою групи";
                throw new OperationCannotBePerformedException(errorMessage);
            }
        }

        String studyYearLastTwoDigits = String.valueOf(studyYear).substring(2);
        int genSequenceNumber = 1, sequenceNumberProf = 1;
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            StringBuilder groupName = new StringBuilder();
            if (selectiveCourse.getTrainingCycle() == TrainingCycle.GENERAL) {
                groupName
                        .append(selectiveCourse.getDegree().getName().substring(0, 1))
                        .append(studyYearLastTwoDigits)
                        .append("-")
                        .append(studentsYear)
                        .append("к")
                        .append("-")
                        .append(genSequenceNumber++);
            } else {
                groupName
                        .append(selectiveCourse.getDegree().getName().substring(0, 1))
                        .append(studyYearLastTwoDigits)
                        .append("-")
                        .append(studentsYear)
                        .append("к")
                        .append("-");

                if (specialFieldsOfKnowledge.contains(selectiveCourse.getFieldOfKnowledge().getCode())) {
                    groupName.append(selectiveCourse.getFieldOfKnowledge().getCode()).append("-").append(sequenceNumberProf++);
                } else {
                    List<SelectiveCoursesStudentDegrees> studentDegreesForSelectiveCourse =
                            selectiveCoursesStudentDegreesRepository.findActiveBySelectiveCourse(selectiveCourse.getId());
                    if (studentDegreesForSelectiveCourse.size() > 0)
                        groupName.append(studentDegreesForSelectiveCourse.get(0).getStudentDegree().getSpecialization().getSpeciality().getCode());
                    else
                        continue;
                }
            }
            selectiveCourse.setGroupName(groupName.toString());
        }
        return selectiveCourses;
    }
}

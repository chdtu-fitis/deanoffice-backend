package ua.edu.chdtu.deanoffice.service.course;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.util.CourseForGroupUpdateHolder;
import ua.edu.chdtu.deanoffice.api.course.util.CourseForStudentUpdateHolder;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.CoursesForStudentsRepository;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.service.*;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCoursesStudentDegreesService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.math.BigDecimal;
import java.util.*;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudentGroupService studentGroupService;
    private final StudentDegreeService studentDegreeService;
    private final CourseNameService courseNameService;
    private final CourseForGroupService courseForGroupService;
    private final GradeRepository gradeRepository;
    private final SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService;
    private final CoursesForStudentsService coursesForStudentsService;
    private final GradeService gradeService;
    private final int ROWS_PER_PAGE = 50;
    private final CoursesForStudentsRepository coursesForStudentsRepository;

    public CourseService(CourseRepository courseRepository, StudentGroupService studentGroupService,
                         CourseNameService courseNameService, CourseForGroupService courseForGroupService,
                         GradeRepository gradeRepository, StudentDegreeService studentDegreeService,
                         SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService, CoursesForStudentsService coursesForStudentsService, GradeService gradeService, CoursesForStudentsRepository coursesForStudentsRepository) {
        this.courseRepository = courseRepository;
        this.studentGroupService = studentGroupService;
        this.courseNameService = courseNameService;
        this.courseForGroupService = courseForGroupService;
        this.gradeRepository = gradeRepository;
        this.studentDegreeService = studentDegreeService;
        this.selectiveCoursesStudentDegreesService = selectiveCoursesStudentDegreesService;
        this.coursesForStudentsService = coursesForStudentsService;
        this.gradeService = gradeService;
        this.coursesForStudentsRepository = coursesForStudentsRepository;
    }

    public Course getCourseByAllAttributes(Course course) {
        return courseRepository.findOne(course.getSemester(), course.getKnowledgeControl().getId(), course.getCourseName().getId(),
                course.getHours(), course.getHoursPerCredit());
    }

    public Course getCourseByAllAttributes(int semester, int knowledgeControlId, int courseNameId,
                                           int hours, int hoursPerCredit) {
        return courseRepository.findOne(semester, knowledgeControlId, courseNameId, hours, hoursPerCredit);
    }

    public List<Course> getCoursesBySemester(int semester) {
        return courseRepository.findAllBySemester(semester);
    }

    public List<Course> getCoursesBySemesterAndHoursPerCredit(int semester, int hoursPerCredit) {
        return courseRepository.findAllBySemesterAndHoursPerCredit(semester, hoursPerCredit);
    }

    public Course createOrUpdateCourse(Course course) {
        return this.courseRepository.save(course);
    }

    public Course getById(int id) {
        return courseRepository.findById(id).get();
    }

    public Course getByCourse(Course course) {
        return courseRepository.findById(course.getId()).get();
    }

    public List<Course> getCoursesByGroupId(Integer groupId) {
        return courseRepository.getByGroupId(groupId);
    }

    public List<ForeignStudentsSynchronizationBean> getForeignStudentsSynchronizationResult() {
        List<ForeignStudentsSynchronizationBean> result = new ArrayList<>();
        Map<Integer, Integer> foreignGroupIdToOtherGroupId = getForeignGroupIdMappedToOtherGroupId(
                studentGroupService.getAllByActive(true, 8),
                studentGroupService.getGroupsMatchingForeignGroups(true)
        );
        for (Integer foreignId : foreignGroupIdToOtherGroupId.keySet()) {
            result.add(getForeignStudentsSynchronizationBeanForGroups(foreignId,
                    foreignGroupIdToOtherGroupId.get(foreignId)));
        }
        return result;
    }

    private Map<Integer, Integer> getForeignGroupIdMappedToOtherGroupId(List<StudentGroup> foreignGroups,
                                                                        List<StudentGroup> otherGroups) {
        Map<Integer, Integer> foreignGroupIdToOtherGroupId = new HashMap<>();
        for (StudentGroup otherGroup : otherGroups) {
            foreignGroups.stream()
                    .filter(foreignGroup ->
                            foreignGroup.getName().replace("ін", "").equals(otherGroup.getName()))
                    .forEach(foreignGroup -> foreignGroupIdToOtherGroupId.put(foreignGroup.getId(), otherGroup.getId()));
        }
        return foreignGroupIdToOtherGroupId;
    }

    private ForeignStudentsSynchronizationBean getForeignStudentsSynchronizationBeanForGroups(Integer foreignIGroupId,
                                                                                              Integer otherGroupId) {
        StudentGroup foreignGroup = studentGroupService.getById(foreignIGroupId);
        StudentGroup otherGroup = studentGroupService.getById(otherGroupId);
        List<Course> coursesForForeignGroup = getCoursesByGroupId(foreignIGroupId);
        List<Course> coursesForOtherGroup = getCoursesByGroupId(otherGroupId);
        List<Course> commonCourses = new ArrayList<>();
        List<Course> differentForeignCourses = new ArrayList<>(coursesForForeignGroup);
        List<Course> differentOtherCourses = new ArrayList<>(coursesForOtherGroup);
        for (Course foreignCourse : coursesForForeignGroup) {
            for (Course otherCourse : coursesForOtherGroup) {
                if (foreignCourse.getId() == otherCourse.getId()) {
                    commonCourses.add(foreignCourse);
                    differentForeignCourses.remove(foreignCourse);
                    differentOtherCourses.remove(otherCourse);
                    break;
                }
            }
        }
        ForeignStudentsSynchronizationBean bean = new ForeignStudentsSynchronizationBean();
        bean.setCommon(commonCourses);
        bean.setDifferentForeignCourses(differentForeignCourses);
        bean.setDifferentOtherCourses(differentOtherCourses);
        bean.setForeignGroup(foreignGroup);
        bean.setOtherGroup(otherGroup);
        return bean;
    }

    public CoursePaginationBean getAllCourses(int page) {
        int totalOfAllCourses = courseRepository.findTotalOfAllCourses();
        int totalPages = (totalOfAllCourses / ROWS_PER_PAGE) + ((totalOfAllCourses % ROWS_PER_PAGE) == 0 ? 0 : 1);
        List<Course> items = courseRepository.findAllCourses(PageRequest.of(page - 1, ROWS_PER_PAGE));
        return new CoursePaginationBean(totalPages, page, items);
    }

    public CoursePaginationBean getCourseByFilters(int page,
                                                   String courseName,
                                                   Integer hours,
                                                   Integer hoursPerCredit,
                                                   String knowledgeControl,
                                                   String nameStartingWith,
                                                   String nameContains,
                                                   Integer semester) {
        Specification<Course> specification = CourseSpecification.getCourseWithImportFilters(
                courseName, hours, hoursPerCredit, knowledgeControl, nameStartingWith, nameContains, semester);
        int totalOfFilteredCourses = (int) courseRepository.count(specification);
        int totalPages = (totalOfFilteredCourses / ROWS_PER_PAGE) + ((totalOfFilteredCourses % ROWS_PER_PAGE) == 0 ? 0 : 1);
        Sort orders = Sort.by(Sort.Direction.ASC, "semester")
                .and(Sort.by(Sort.Direction.ASC, "knowledgeControl"))
                .and(Sort.by(Sort.Direction.ASC, "courseName"));
        PageImpl items = courseRepository.findAll(specification, PageRequest.of(page - 1, ROWS_PER_PAGE, orders));
        return new CoursePaginationBean(totalPages, page, items.getContent());
    }

    public CoursePaginationBean getPaginatedUnusedCourses(int page) {
        int totalOfUnusedCourses = courseRepository.findTotalOfUnusedCourses();
        int totalPages = (totalOfUnusedCourses / ROWS_PER_PAGE) + ((totalOfUnusedCourses % ROWS_PER_PAGE) == 0 ? 0 : 1);
        List<Course> items = courseRepository.findUnusedCourses(PageRequest.of(page - 1, ROWS_PER_PAGE));
        return new CoursePaginationBean(totalPages, page, items);
    }

    public void deleteCoursesByIds(List<Integer> ids) {
        courseRepository.deleteByIdIn(ids);
    }

    public List<Course> getCoursesWithWrongCredits() {
        return courseRepository.findCoursesWithWrongCredits();
    }

    public void updateCoursesCreditsByIds(List<Integer> ids) {
        for (Integer id : ids) {
            Course course = getById(id);
            double correctCredits = Math.abs((0.0 + course.getHours()) / course.getHoursPerCredit());
            course.setCredits(new BigDecimal(correctCredits));
            createOrUpdateCourse(course);
        }
    }

    public List<Course> getCoursesByCourseNameId(int id) {
        return courseRepository.findCoursesByCourseNameId(id);
    }

    @Transactional
    public void mergeCourseNamesByIdToId(Map<Integer, List<Integer>> idToId) throws OperationCannotBePerformedException {
        for (Integer correctId : idToId.keySet()) {
            CourseName correctCourseName = courseNameService.getCourseNameById(correctId);
            for (Integer wrongId : idToId.get(correctId)) {
                if (correctId.equals(wrongId)) {
                    throw new OperationCannotBePerformedException("id правильної назви предмета дорівнює id неправильної назви предмета");
                }
                for (Course wrongCourse : getCoursesByCourseNameId(wrongId)) {
                    Course correctCourse = getCourseByAllAttributes(wrongCourse.getSemester(),
                            wrongCourse.getKnowledgeControl().getId(), correctCourseName.getId(),
                            wrongCourse.getHours(), wrongCourse.getHoursPerCredit());
                    if (correctCourse != null) {
                        courseForGroupService.updateCourseIdById(correctCourse.getId(), wrongCourse.getId());
                        gradeRepository.updateCourseIdByCourseId(correctCourse.getId(), wrongCourse.getId());
                        courseRepository.delete(wrongCourse);
                    } else {
                        courseRepository.updateCourseNameIdInCourse(correctId, wrongId, wrongCourse.getId());
                    }
                }
                courseNameService.deleteCourseNameById(wrongId);
            }
        }
    }

    public List<StudentCourseBean>[] getStudentCoursesListInStudyYear(StudentDegree studentDegree, Integer studyYear) throws OperationCannotBePerformedException {
        if (studentDegree == null || studentDegree.getStudentGroup() == null)
            throw new OperationCannotBePerformedException("Студент не існує або йому не призначено групу");
        StudentGroup studentGroup = studentDegree.getStudentGroup();
        int realStudyYear = studentDegreeService.getRealStudentDegreeYear(studentGroup, studyYear);
        List<StudentCourseBean>[] allCoursesInYear = new ArrayList[2];
        List<StudentCourseBean> regularCoursesInSemester1 = getRegularCoursesInSemester(studentGroup, studyYear * 2 - 1);
        List<StudentCourseBean> selectiveCoursesInSemester1 = getSelectiveCoursesInSemester(studentDegree.getId(), realStudyYear * 2 - 1);
        regularCoursesInSemester1.addAll(selectiveCoursesInSemester1);
        allCoursesInYear[0] = regularCoursesInSemester1;
        List<StudentCourseBean> regularCoursesInSemester2 = getRegularCoursesInSemester(studentGroup, studyYear * 2);
        List<StudentCourseBean> selectiveCoursesInSemester2 = getSelectiveCoursesInSemester(studentDegree.getId(), realStudyYear * 2);
        regularCoursesInSemester2.addAll(selectiveCoursesInSemester2);
        allCoursesInYear[1] = regularCoursesInSemester2;
        return allCoursesInYear;
    }

    private List<StudentCourseBean> getRegularCoursesInSemester(StudentGroup studentGroup, int semester) {
        List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semester);
        List<StudentCourseBean> studentCourseBeans = new ArrayList<>();
        coursesForGroup.forEach(cfg -> {
            Course c = cfg.getCourse();
            Teacher t = cfg.getTeacher();
            StudentCourseBean studentCourseBean = createStudentCourseBean(c, t, false);
            studentCourseBeans.add(studentCourseBean);
        });
        return studentCourseBeans;
    }

    private List<StudentCourseBean> getSelectiveCoursesInSemester(int studentDegreeId, int semester) {
        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees =
                selectiveCoursesStudentDegreesService.getSelectiveCoursesByStudentDegreeIdAndSemester(studentDegreeId, semester);
        List<StudentCourseBean> studentCourseBeans = new ArrayList<>();
        selectiveCoursesStudentDegrees.forEach(scsd -> {
            Course c = scsd.getSelectiveCourse().getCourse();
            Teacher t = scsd.getSelectiveCourse().getTeacher();
            StudentCourseBean studentCourseBean = createStudentCourseBean(c, t, true);
            studentCourseBeans.add(studentCourseBean);
        });
        return studentCourseBeans;
    }

    private StudentCourseBean createStudentCourseBean(Course c, Teacher t, boolean isSelective) {
        return new StudentCourseBean(c.getCourseName().getName(), c.getHours(),
                c.getCredits(), c.getSemester(), t != null ? t.getName() + " " + t.getSurname() : "",
                c.getKnowledgeControl().getName(), isSelective);
    }

    public void adjustNationalGrade(int oldKnowledgeControlId, int newKnowledgeControlId, int newCourseId) {
        Map<String, Boolean> gradeDefinition = gradeService.evaluateGradedChange(oldKnowledgeControlId, newKnowledgeControlId);
        if (gradeDefinition.get(GradeService.NEW_GRADED_VALUE) != null) {
            if (gradeDefinition.get(GradeService.NEW_GRADED_VALUE)) {
                gradeService.updateNationalGradeByCourseIdAndGradedTrue(newCourseId);
            } else {
                gradeService.updateNationalGradeByCourseIdAndGradedFalse(newCourseId);
            }
        }
    }

    public Course updateCourseName(CourseName courseName, Course newCourse) {
        CourseName courseNameFromDB = courseNameService.getCourseNameByName(courseName.getName());
        if (courseNameFromDB != null) {
            newCourse.setCourseName(courseNameFromDB);
        } else {
            CourseName newCourseName = new CourseName();
            newCourseName.setName(courseName.getName());
            newCourse.setCourseName(courseNameService.saveCourseName(newCourseName));
        }
        return newCourse;
    }

    public CourseDTO updateCourse(int groupId, CourseForGroupUpdateHolder coursesForGroupHolder) throws UnauthorizedFacultyDataException, OperationCannotBePerformedException {
        CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(coursesForGroupHolder.getCourseForGroupId());
        if (courseForGroup.getStudentGroup().getSpecialization().getFaculty().getId() != FacultyUtil.getUserFacultyIdInt()
                || courseForGroup.getStudentGroup().getId() != groupId) {
            throw new UnauthorizedFacultyDataException();
        }
        if (courseForGroup.getCourse().getCourseName().getId() == coursesForGroupHolder.getNewCourse().getCourseName().getId()
                && courseForGroup.getCourse().getHoursPerCredit().equals(coursesForGroupHolder.getNewCourse().getHoursPerCredit())
                && courseForGroup.getCourse().getHours().equals(coursesForGroupHolder.getNewCourse().getHours())
                && courseForGroup.getCourse().getKnowledgeControl().getId() == coursesForGroupHolder.getNewCourse().getKnowledgeControl().getId()) {
            throw new OperationCannotBePerformedException("Не змінено жодного атрибуту предмету");
        }

        Course newCourse = map(coursesForGroupHolder.getNewCourse(), Course.class);
        int oldCourseId = coursesForGroupHolder.getOldCourseId();
        Course oldCourse = getById(oldCourseId);
        Course courseFromDb = getCourseByAllAttributes(newCourse);
        if (courseFromDb != null) {
            newCourse = courseFromDb;
            double correctCredits = Math.abs((0.0 + courseFromDb.getHours()) / courseFromDb.getHoursPerCredit());
            if (Math.abs(correctCredits - courseFromDb.getCredits().doubleValue()) > 0.005) {
                courseFromDb.setCredits(new BigDecimal(correctCredits));
                createOrUpdateCourse(courseFromDb);
            }
            courseForGroupService.updateCourseInCoursesForGroupsAndGrade(courseForGroup, courseFromDb, oldCourseId, groupId, oldCourse.getKnowledgeControl().getId());
        } else {
            CourseName courseName = map(coursesForGroupHolder.getNewCourse().getCourseName(), CourseName.class);
            newCourse = updateCourseName(courseName, newCourse);
            if (courseForGroupService.hasSoleCourse(oldCourseId)) {
                int oldKnowledgeControlId = oldCourse.getKnowledgeControl().getId();
                int newKnowledgeControlId = newCourse.getKnowledgeControl().getId();
                createOrUpdateCourse(newCourse);
                adjustNationalGrade(oldKnowledgeControlId, newKnowledgeControlId, newCourse.getId());
            } else {
                newCourse.setId(0);
                newCourse = createOrUpdateCourse(newCourse);
                courseForGroupService.updateCourseInCoursesForGroupsAndGrade(courseForGroup, newCourse, oldCourseId, groupId, oldCourse.getKnowledgeControl().getId());
            }
        }
        return map(newCourse, CourseDTO.class);
    }

    public CourseDTO updateCourse(int studentDegreeId, CourseForStudentUpdateHolder courseForStudentUpdateHolder) throws UnauthorizedFacultyDataException, OperationCannotBePerformedException, NotFoundException {
        Course oldCourse = getById(courseForStudentUpdateHolder.getOldCourseId());
        Course newCourse = map(courseForStudentUpdateHolder.getNewCourse(), Course.class);
        if (!Objects.equals(oldCourse.getCourseName().getName(), newCourse.getCourseName().getName()) || !Objects.equals(oldCourse.getCourseName().getNameEng(), newCourse.getCourseName().getNameEng())) {
            CourseName courseName = map(courseForStudentUpdateHolder.getNewCourse().getCourseName(), CourseName.class);
            newCourse = updateCourseName(courseName, newCourse);
        }
        Course courseFromDb = getCourseByAllAttributes(newCourse);
        if(!coursesForStudentsRepository.existsByCourseIdAndStudentDegreeId(courseForStudentUpdateHolder.getOldCourseId(), studentDegreeId)) {
            throw new NotFoundException("Неправильно заданий параметр курсу чи студента");
        }
        if (oldCourse.getCourseName().getId() == newCourse.getCourseName().getId()
                && oldCourse.getHoursPerCredit().equals(newCourse.getHoursPerCredit())
                && oldCourse.getHours().equals(newCourse.getHours())
                && oldCourse.getKnowledgeControl().getId() == newCourse.getKnowledgeControl().getId()) {
            throw new OperationCannotBePerformedException("Не змінено жодного атрибуту предмету");
        }

        if (courseFromDb != null) {
            newCourse = fixCredits(courseFromDb);
        } else {
            newCourse.setId(0);
            fixCredits(newCourse);
        }
        newCourse = createOrUpdateCourse(newCourse);
        coursesForStudentsRepository.updateByCourseIdAndStudentDegreeId(studentDegreeId, newCourse.getId(), oldCourse.getId());
        return map(newCourse, CourseDTO.class);
    }

    @NotNull
    private Course fixCredits(Course fixingCourse) {
        double correctCredits = Math.abs((0.0 + fixingCourse.getHours()) / fixingCourse.getHoursPerCredit());
        if (Math.abs(correctCredits - fixingCourse.getCredits().doubleValue()) > 0.005) {
            fixingCourse.setCredits(new BigDecimal(correctCredits));
        }
        return fixingCourse;
    }
}
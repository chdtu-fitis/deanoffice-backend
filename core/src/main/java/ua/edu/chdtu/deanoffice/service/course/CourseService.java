package ua.edu.chdtu.deanoffice.service.course;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudentGroupService studentGroupService;
    private final int ROWS_PER_PAGE = 50;

    public CourseService(CourseRepository courseRepository, StudentGroupService studentGroupService) {
        this.courseRepository = courseRepository;
        this.studentGroupService = studentGroupService;
    }

    public Course getCourseByAllAttributes(Course course) {
        return courseRepository.findOne(course.getSemester(), course.getKnowledgeControl().getId(), course.getCourseName().getId(),
                course.getHours(), course.getHoursPerCredit());
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
        return courseRepository.findOne(id);
    }

    public Course getByCourse(Course course) {
        return courseRepository.findOne(course.getId());
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
        List<Course> items = courseRepository.findAllCourses(new PageRequest(page, ROWS_PER_PAGE));
        return new CoursePaginationBean(totalPages, page - 1, items);
    }

    public CoursePaginationBean getCourseByFilters(int page,
                                                   String courseName,
                                                   Integer hours,
                                                   Integer hoursPerCredit,
                                                   String knowledgeControl,
                                                   String nameStartingWith,
                                                   String nameContains) {
        Specification<Course> specification = CourseSpecification.getCourseWithImportFilters(
                courseName, hours, hoursPerCredit, knowledgeControl, nameStartingWith, nameContains);
        int totalOfFilteredCourses = (int) courseRepository.count(specification);
        int totalPages = (totalOfFilteredCourses / ROWS_PER_PAGE) + ((totalOfFilteredCourses % ROWS_PER_PAGE) == 0 ? 0 : 1);
        Sort orders = new Sort(Sort.Direction.ASC, "semester")
                .and(new Sort(Sort.Direction.ASC, "knowledgeControl"))
                .and(new Sort(Sort.Direction.ASC, "courseName"));
        PageImpl items = courseRepository.findAll(specification, new PageRequest(page - 1, ROWS_PER_PAGE, orders));
        return new CoursePaginationBean(totalPages, page, items.getContent());
    }

    public CoursePaginationBean getPaginatedUnusedCourses(int page) {
        int totalOfUnusedCourses = courseRepository.findTotalOfUnusedCourses();
        int totalPages = (totalOfUnusedCourses / ROWS_PER_PAGE) + ((totalOfUnusedCourses % ROWS_PER_PAGE) == 0 ? 0 : 1);
        List<Course> items = courseRepository.findUnusedCourses(new PageRequest(page - 1, ROWS_PER_PAGE));
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
}

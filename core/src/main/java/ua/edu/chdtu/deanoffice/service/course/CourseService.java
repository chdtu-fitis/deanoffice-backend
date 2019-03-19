package ua.edu.chdtu.deanoffice.service.course;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

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

    public List<Course> getCoursesBySemesterAndHoursPerCredit(int semester, int hoursPerCredit){
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

    public List<Course> getAllCourses() {
        return courseRepository.findAllCourses();
    }

    //doesn't work
    public CoursePaginationBean getCourseByFilters(int page, String courseName, int hours, int hoursPerCredit, String knowledgeControl,
                                                   String nameStartingWith, String nameMatches) {
        int totalPages = 13;
        PageRequest pageRequest = new PageRequest(page, ROWS_PER_PAGE);
        List<Course> items = courseRepository.findAll(CourseSpecification.getCourseWithImportFilters(
                courseName, hours, hoursPerCredit, knowledgeControl, nameStartingWith, nameMatches), pageRequest);
        return new CoursePaginationBean(totalPages, page, items);
    }

    public CoursePaginationBean getPaginatedUnusedCourses(int page) {
        int totalOfUnusedCourses = courseRepository.findTotalOfUnusedCourses();
        int totalPages = (totalOfUnusedCourses / ROWS_PER_PAGE) + ((totalOfUnusedCourses % ROWS_PER_PAGE) == 0 ? 0 : 1);
        List<Course> items = courseRepository.findUnusedCourses(new PageRequest(page, ROWS_PER_PAGE));
        return new CoursePaginationBean(totalPages, page, items);
    }

    public void deleteCoursesByIds(List<Integer> ids) {
        courseRepository.deleteByIdIn(ids);
    }

//    private long getTotalCoursesByFilters(String courseName, int hours, int hoursPerCredit, String knowledgeControl,
//                                          String nameStartingWith, String nameMatches) {
//        return courseRepository.countAll(CourseSpecification.getCourseWithImportFilters(
//                courseName, hours, hoursPerCredit, knowledgeControl, nameStartingWith, nameMatches));
//    }
}

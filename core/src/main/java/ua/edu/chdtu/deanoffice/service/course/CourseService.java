package ua.edu.chdtu.deanoffice.service.course;

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
}

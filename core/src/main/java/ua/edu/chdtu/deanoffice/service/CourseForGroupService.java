package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.repository.CourseForGroupRepository;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;
import ua.edu.chdtu.deanoffice.entity.Course;

import java.util.List;

@Service
public class CourseForGroupService {
    private final CourseForGroupRepository courseForGroupRepository;
    private final StudentGroupRepository studentGroupRepository;

    public CourseForGroupService(CourseForGroupRepository courseForGroupRepository, StudentGroupRepository studentGroupRepository) {
        this.courseForGroupRepository = courseForGroupRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<CourseForGroup> getCourseForGroup(int idGroup) {
        return courseForGroupRepository.findAllByStudentGroupId(idGroup);
    }

    public CourseForGroup getCourseForGroup(int groupId, int courseId) {
        return courseForGroupRepository.findByStudentGroupIdAndCourseId(groupId, courseId);
    }

    public List<CourseForGroup> getCoursesForGroupBySemester(int idGroup, int semester) {
        return courseForGroupRepository.findAllByStudentGroupIdAndCourse_Semester(idGroup, semester);
    }

    public List<CourseForGroup> getCourseForGroupBySpecialization(int specialization, int semester){
        return courseForGroupRepository.findAllBySpecialization(specialization, semester);
    }

    public List<CourseForGroup> getCoursesForGroupBySemester(int semester){
        return courseForGroupRepository.findAllBySemester(semester);
    }

    public void addCourseForGroupAndNewChanges(List<CourseForGroup> newCourses, List<CourseForGroup> mutableCourses, List<String> deleteCoursesIdList, int idGroup){
        StudentGroup studentGroup = this.studentGroupRepository.findOne(idGroup);
        if (newCourses !=null){
            for (CourseForGroup course : newCourses) {
                CourseForGroup courseForGroup = new CourseForGroup();
                courseForGroup.setCourse(course.getCourse());
                courseForGroup.setStudentGroup(studentGroup);
                courseForGroup.setTeacher(course.getTeacher());
                courseForGroup.setExamDate(course.getExamDate());
                courseForGroupRepository.save(courseForGroup);
            }
        }

        if (mutableCourses != null){
            for (CourseForGroup course : mutableCourses) {
                CourseForGroup mutableCourse = courseForGroupRepository.findOne(course.getId());
                mutableCourse.setCourse(course.getCourse());
                mutableCourse.setStudentGroup(course.getStudentGroup());
                mutableCourse.setTeacher(course.getTeacher());
                mutableCourse.setExamDate(course.getExamDate());
                courseForGroupRepository.save(mutableCourse);
            }
        }

        if (deleteCoursesIdList != null){
            for (String courseId : deleteCoursesIdList) {
                CourseForGroup deleteCourse = courseForGroupRepository.findOne(Integer.parseInt(courseId));
                courseForGroupRepository.delete(deleteCourse);
            }
        }
    }
}

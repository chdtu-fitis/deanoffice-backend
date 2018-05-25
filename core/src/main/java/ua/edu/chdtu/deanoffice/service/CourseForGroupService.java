package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CourseForGroupRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;
import java.util.Set;

@Service
public class CourseForGroupService {
    private final CourseForGroupRepository courseForGroupRepository;
    private final StudentGroupRepository studentGroupRepository;

    public CourseForGroupService(CourseForGroupRepository courseForGroupRepository, StudentGroupRepository studentGroupRepository) {
        this.courseForGroupRepository = courseForGroupRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public CourseForGroup getCourseForGroup(int id) {
        return courseForGroupRepository.findOne(id);
    }

    public List<CourseForGroup> getCoursesForOneGroup(int idGroup) {
        return courseForGroupRepository.findAllByStudentGroupId(idGroup);
    }

    public CourseForGroup getCourseForGroup(int groupId, int courseId) {
        return courseForGroupRepository.findByStudentGroupIdAndCourseId(groupId, courseId);
    }

    public List<CourseForGroup> getCoursesForGroupBySemester(int idGroup, int semester) {
        return courseForGroupRepository.findAllByStudentGroupIdAndCourse_Semester(idGroup, semester);
    }

    public List<CourseForGroup> getCourseForGroupBySpecialization(int specialization, int semester) {
        return courseForGroupRepository.findAllBySpecialization(specialization, semester);
    }

    public List<CourseForGroup> getCoursesForGroupBySemester(int semester) {
        return courseForGroupRepository.findAllBySemester(semester);
    }

    public void addCourseForGroupAndNewChanges(
            Set<CourseForGroup> newCourses,
            Set<CourseForGroup> updatedCourses,
            List<Integer> deleteCoursesIds
    ) {
        courseForGroupRepository.save(newCourses);
        courseForGroupRepository.save(updatedCourses);
        for (Integer courseId : deleteCoursesIds) {
            courseForGroupRepository.delete(courseId);
        }
    }

    public void save(CourseForGroup courseForGroup){
        this.courseForGroupRepository.save(courseForGroup);
    }

    public int countByGroup(StudentGroup group){
        return courseForGroupRepository.countByStudentGroup(group);
    }
}

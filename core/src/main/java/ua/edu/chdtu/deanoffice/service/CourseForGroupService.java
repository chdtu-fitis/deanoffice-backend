package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.repository.CourseForGroupRepository;

import java.util.List;

@Service
public class CourseForGroupService {
    private final CourseForGroupRepository courseForGroupRepository;

    public CourseForGroupService(CourseForGroupRepository courseForGroupRepository) {
        this.courseForGroupRepository = courseForGroupRepository;
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
}

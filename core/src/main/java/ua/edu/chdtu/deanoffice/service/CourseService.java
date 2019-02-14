package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
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

    public Course getByCourse(Course course){
        return courseRepository.findOne(course.getId());
    }

    public List<Course> getCoursesByGroupId(Integer groupId){ return courseRepository.getByGroupId(groupId);}
}

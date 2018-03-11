package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

@Service
public class CourseService {
    private final StudentGroupRepository studentGroupRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, StudentGroupRepository studentGroupRepository) {
        this.courseRepository = courseRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<Course> getCourseById(int idCourse) {
        List<Course> courses = courseRepository.findCourseById(idCourse);
        return courses;
    }

    public List<StudentGroup> getGroupsByCourse(int courseId) {
        List<StudentGroup> studentGroups = studentGroupRepository.findAllByCourse(courseId);
        return studentGroups;
    }

    public Course getCourse(int id) {
        return courseRepository.findOne(id);
    }
}

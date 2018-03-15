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

    //TODO StudentGroupRepository краще винести в свій сервіс. Непотрібно перемішувати в кашу сервіси, краще розділяти
    // їх відповідальність
    @Autowired
    public CourseService(CourseRepository courseRepository, StudentGroupRepository studentGroupRepository) {
        this.courseRepository = courseRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    //TODO Потрібно прибрати
    public List<Course> getCourseById(int idCourse) {
        List<Course> courses = courseRepository.findCourseById(idCourse);
        return courses;
    }

    //TODO краще прибрати проміжний результат та винести цей метод в свій сервіс
    public List<StudentGroup> getGroupsByCourse(int courseId) {
        List<StudentGroup> studentGroups = studentGroupRepository.findAllByCourse(courseId);
        return studentGroups;
    }

    //TODO потрібно прибрати
    public Course getCourse(int id) {
        return courseRepository.findOne(id);
    }
}

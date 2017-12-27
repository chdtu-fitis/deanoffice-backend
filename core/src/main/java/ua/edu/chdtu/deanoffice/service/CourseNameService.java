package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.repository.CourseNameRepository;

import java.util.List;

@Service
public class CourseNameService {
    private final CourseNameRepository courseNameRepository;

    @Autowired
    public CourseNameService(CourseNameRepository courseNameRepository) {
        this.courseNameRepository = courseNameRepository;
    }
    public List<CourseName> getCourseNameById(int idCourseName){
        List<CourseName> courseNames = courseNameRepository.findCourseNameById(idCourseName);
        return courseNames;
    }
}

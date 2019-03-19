package ua.edu.chdtu.deanoffice.service.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.repository.CourseNameRepository;

import java.util.List;

@Service
public class CourseNameService {
    private final CourseNameRepository courseNameRepository;

    public CourseNameService(CourseNameRepository courseNameRepository) {
        this.courseNameRepository = courseNameRepository;
    }

    public List<CourseName> getCourseNames() {
        return this.courseNameRepository.findAll();
    }

    public CourseName saveCourseName(CourseName courseName) {
        return this.courseNameRepository.save(courseName);
    }

    public CourseName getCourseNameByName(String name) {
        return this.courseNameRepository.findByName(name);
    }

    public List<CourseName> getUnusedCoursesNames() {
        return courseNameRepository.findUnusedCoursesNames();
    }

    public void deleteCoursesNamesByIds(List<Integer> ids) {
        courseNameRepository.deleteCourseNameById(ids);
    }
}

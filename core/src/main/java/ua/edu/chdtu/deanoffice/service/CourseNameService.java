package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.repository.CourseNameRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseNameService {
    @Autowired
    private CourseNameRepository courseNameRepository;

    @Autowired
    private CurrentYearService currentYearService;

    public List<CourseName> getCourseNames(){
        return this.courseNameRepository.findAll();
    }

    public CourseName saveCourseName(CourseName courseName){
        return this.courseNameRepository.save(courseName);
    }

    public CourseName getCourseNameByName(String name){
        return this.courseNameRepository.findByName(name);
    }

    public Map<String, String> getGraduatesCoursesWithEmptyEngName(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<Object[]> coursesName = courseNameRepository.findAllForGraduatesWithNoEnglishName(year, facultyId, degreeId);
        return coursesName
                .stream()
                .collect(Collectors.toMap(cn -> (String)cn[0], cn -> cn[1] + "; ", (e1, e2) -> (e1 + e2), LinkedHashMap::new));
    }
}

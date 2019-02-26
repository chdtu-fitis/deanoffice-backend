package ua.edu.chdtu.deanoffice.service;

import com.google.common.base.Strings;
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

    public Map<CourseName, String> getAllCoursesWhereEngNameIsNullOrEmpty(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<CourseName> coursesName = courseNameRepository.findAllForGraduates(year, facultyId, degreeId);
        return coursesName
                .stream()
                .filter(cn -> !checkNameEng(cn).equals(""))
                .collect(Collectors.toMap(cn -> cn, this::checkNameEng, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private String checkNameEng(CourseName courseName){
        String message = "";
        message += Strings.isNullOrEmpty(courseName.getNameEng()) ? "Відсутня англійська назва. " : "";
        return message;
    }
}

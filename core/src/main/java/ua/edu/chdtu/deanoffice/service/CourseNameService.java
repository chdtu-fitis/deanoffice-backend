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

    public Map<String, String> getAllCoursesWhereEngNameIsNullOrEmpty(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<Object[]> coursesName = courseNameRepository.findAllForGraduates(year, facultyId, degreeId);
        return coursesName
                .stream()
                .filter(cn -> !checkNameEng(cn).equals(""))
                .collect(Collectors.toMap(cn -> (String)cn[0], this::checkNameEng, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private String checkNameEng(Object item[]){
        String message = "";
        message += Strings.isNullOrEmpty((String)item[1]) ? String.format("Група: %s Семестр: %d",
                (String) item[2], (Integer) item[3]) : "";
        return message;
    }
}

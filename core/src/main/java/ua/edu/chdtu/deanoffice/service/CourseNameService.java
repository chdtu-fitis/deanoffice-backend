package ua.edu.chdtu.deanoffice.service;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.repository.CourseNameRepository;
import java.util.ArrayList;
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
        List<CourseNameWithGroupNameAndSemester> courseNameWithGroupNameAndSemesters = new ArrayList<>(coursesName.size());
        coursesName.forEach(item ->
            courseNameWithGroupNameAndSemesters.add(
                    new CourseNameWithGroupNameAndSemester((String)item[0], (String)item[1],
                                                            (String)item[2], (Integer) item[3])));
        return courseNameWithGroupNameAndSemesters
                .stream()
                .filter(cn -> !checkNameEng(cn).equals(""))
                .collect(Collectors.toMap(cn -> cn.getGroupName(), this::checkNameEng, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private String checkNameEng(CourseNameWithGroupNameAndSemester cn){
        String message = "";
        message += Strings.isNullOrEmpty(cn.getNameEng()) ? String.format("Група: %s Семестр: %d",
                cn.getGroupName(), cn.getSemester()) : "";
        return message;
    }
}

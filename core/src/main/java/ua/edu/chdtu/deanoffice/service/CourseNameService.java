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

    public Map<String, String> getAllCoursesWhereEngNameIsNullOrEmpty(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<Object[]> coursesName = courseNameRepository.findAllForGraduates(year, facultyId, degreeId);
        changeCoursesNameListForMapping(coursesName);
        return coursesName
                .stream()
                .collect(Collectors.toMap(cn -> (String)cn[0], cn -> (String)cn[1], (e1, e2) -> e2, LinkedHashMap::new));
    }

    private void changeCoursesNameListForMapping (List<Object[]> coursesName) {
        int i=0;
        String message = "";
        for(Object courseName[]: coursesName) {
            if(i == 0 || !courseName[0].equals(coursesName.get(i-1)[0]))
                message = String.format("Група: %s. ", (String)courseName[1]);
                else {
                    message += String.format("Група: %s. ", (String) courseName[1]);
                    courseName[1] = message;
                }
            i++;
        }
    }
}

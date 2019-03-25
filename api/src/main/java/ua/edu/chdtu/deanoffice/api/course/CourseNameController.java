package ua.edu.chdtu.deanoffice.api.course;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseNameDTO;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.service.course.CourseNameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course-names")
public class CourseNameController {
    private CourseNameService courseNameService;

    public CourseNameController(CourseNameService courseNameService) {
        this.courseNameService = courseNameService;
    }

    @GetMapping("/unused")
    public ResponseEntity getUnusedCoursesNames() {
        try {
            List<CourseName> unusedCoursesNames = courseNameService.getUnusedCoursesNames();
            return ResponseEntity.ok(Mapper.strictMap(unusedCoursesNames, CourseNameDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteCoursesNamesByIds(@RequestParam("ids") List<Integer> ids) {
        try {
            courseNameService.deleteCoursesNamesByIds(ids);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/similar")
    public ResponseEntity getSimilarCoursesNames() {
        Map<CourseName, List<CourseName>> similarCoursesNames = courseNameService.getSimilarCoursesNames();
        List<List<CourseName>> mappedList = new ArrayList<>();
        similarCoursesNames.keySet()
                .forEach(key -> {
                    ArrayList<CourseName> currentList = new ArrayList<>();
                    currentList.add(key);
                    currentList.addAll(similarCoursesNames.get(key));
                    mappedList.add(currentList);
                });
        List<List<NamedDTO>> resultDTOs = new ArrayList<>();
        for (List<CourseName> list : mappedList) {
            resultDTOs.add(Mapper.map(list, NamedDTO.class));
        }
        return ResponseEntity.ok(resultDTOs);
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, CourseController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}

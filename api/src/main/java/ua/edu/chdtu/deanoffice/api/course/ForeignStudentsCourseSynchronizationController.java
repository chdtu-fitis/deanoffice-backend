package ua.edu.chdtu.deanoffice.api.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.dto.ForeignStudentsSynchronizationDTO;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.course.ForeignStudentsSynchronizationBean;

import java.util.List;


@RestController
@RequestMapping("/courses")
public class ForeignStudentsCourseSynchronizationController {

    private CourseService courseService;

    @Autowired
    public ForeignStudentsCourseSynchronizationController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/foreign/synchronization")
    public ResponseEntity synchronization() {
        try {
            List<ForeignStudentsSynchronizationBean> foreignStudentsSynchronizationResult = courseService.getForeignStudentsSynchronizationResult();
            List<ForeignStudentsSynchronizationDTO> map = Mapper.strictMap(foreignStudentsSynchronizationResult, ForeignStudentsSynchronizationDTO.class);
            return ResponseEntity.ok(map);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception,
                ForeignStudentsCourseSynchronizationController.class,
                ExceptionToHttpCodeMapUtil.map(exception));
    }
}


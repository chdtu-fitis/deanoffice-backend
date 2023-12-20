package ua.edu.chdtu.deanoffice.api.document.diplomasupplement;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.CourseNameService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documents/supplements")
public class DiplomaSupplementController extends DocumentResponseController {

    private DiplomaSupplementService diplomaSupplementService;
    private FacultyService facultyService;
    private StudentDegreeService studentDegreeService;
    private CourseNameService courseNameService;

    public DiplomaSupplementController(DiplomaSupplementService diplomaSupplementService,
                                       FacultyService facultyService,
                                       StudentDegreeService studentDegreeService, CourseNameService courseNameService) {
        this.diplomaSupplementService = diplomaSupplementService;
        this.facultyService = facultyService;
        this.studentDegreeService = studentDegreeService;
        this.courseNameService = courseNameService;
    }

    @GetMapping("/degrees/{studentDegreeId}/docx")
    public ResponseEntity<Resource> generateDocxForStudent(@PathVariable Integer studentDegreeId,
                                                           @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File studentDiplomaSupplement = diplomaSupplementService.formDiplomaSupplement(studentDegreeId, FileFormatEnum.DOCX);
            return buildDocumentResponseEntity(studentDiplomaSupplement, studentDiplomaSupplement.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/degrees/{studentDegreeId}/pdf")
    public ResponseEntity<Resource> generatePdfForStudent(@PathVariable Integer studentDegreeId,
                                                          @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File studentDiplomaSupplement = diplomaSupplementService.formDiplomaSupplement(studentDegreeId, FileFormatEnum.PDF);
            return buildDocumentResponseEntity(studentDiplomaSupplement, studentDiplomaSupplement.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/check-courses-translation")
    public ResponseEntity checkEnglishTranslationForCourses(@RequestParam Integer degreeId,
                                                            @CurrentUser ApplicationUser user){
        try {
            Map<String, String> coursesWithoutEnglishTranslation =
                    courseNameService.getGraduatesCoursesWithEmptyEngName(user.getFaculty().getId(), degreeId);
//            String courseNames = "";
//            String whereClause = "";
//            for (Map.Entry<String, String> entry : coursesWithoutEnglishTranslation.entrySet()) {
//                whereClause += entry.getKey()+"\n";
//                courseNames = courseNames+"name='"+entry.getKey() + "' OR ";
//            }
//            System.out.println(whereClause);
//            System.out.println(courseNames);
            List<CourseNameWithoutEngDTO> courseNamesWithoutEngDTOList = new ArrayList<>();
            for (Map.Entry<String, String> entry : coursesWithoutEnglishTranslation.entrySet())
                courseNamesWithoutEngDTOList.add(new CourseNameWithoutEngDTO(entry.getKey(), entry.getValue()));
            return ResponseEntity.ok(courseNamesWithoutEngDTOList);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping("/data-check")
    public ResponseEntity checkDataAvailability(@RequestParam Integer degreeId,
                                                @CurrentUser ApplicationUser user) {
        try {
            Map<StudentDegree, String> studentDegreesWithEmpty = studentDegreeService.checkAllGraduatesData(user.getFaculty().getId(), degreeId);
            List<StudentDataCheckDto> studentDataCheckDtoList = new ArrayList<>();

            for (Map.Entry<StudentDegree, String> entry: studentDegreesWithEmpty.entrySet()) {
                StudentDegree studentDegree = entry.getKey();
                String message = entry.getValue();
                StudentDataCheckDto studentDataCheckDto = new StudentDataCheckDto();
                studentDataCheckDto.setSurname(studentDegree.getStudent().getSurname());
                studentDataCheckDto.setName(studentDegree.getStudent().getName());
                studentDataCheckDto.setPatronimic(studentDegree.getStudent().getPatronimic());
                studentDataCheckDto.setGroupName(studentDegree.getStudentGroup().getName());
                studentDataCheckDto.setMessage(message);
                studentDataCheckDtoList.add(studentDataCheckDto);
            }
            return ResponseEntity.ok(studentDataCheckDtoList);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/grade-check")
    public ResponseEntity checkGradeAvailability(@RequestParam Integer degreeId,
                                                @CurrentUser ApplicationUser user) {
        try {
            Map<StudentDegree, String> studentDegreesWithEmpty = studentDegreeService.checkAllGraduatesGrades(user.getFaculty().getId(), degreeId);
            List<StudentDataCheckDto> studentDataCheckDtoList = new ArrayList<>();
            for (Map.Entry<StudentDegree, String> entry: studentDegreesWithEmpty.entrySet()) {
                StudentDegree studentDegree = entry.getKey();
                String messages = entry.getValue();
                StudentDataCheckDto studentDataCheckDto = new StudentDataCheckDto();
                studentDataCheckDto.setSurname(studentDegree.getStudent().getSurname());
                studentDataCheckDto.setName(studentDegree.getStudent().getName());
                studentDataCheckDto.setPatronimic(studentDegree.getStudent().getPatronimic());
                studentDataCheckDto.setGroupName(studentDegree.getStudentGroup().getName());
                studentDataCheckDto.setMessage(messages);
                studentDataCheckDtoList.add(studentDataCheckDto);
            }
            return ResponseEntity.ok(studentDataCheckDtoList);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, DiplomaSupplementController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}

package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentExpelDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice.handleException;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/students")
public class StudentExpelController {
    private final StudentDegreeService studentDegreeService;
    private final OrderReasonService orderReasonService;

    @Autowired
    public StudentExpelController(
            StudentDegreeService studentDegreeService,
            OrderReasonService orderReasonService
    ) {
        this.studentDegreeService = studentDegreeService;
        this.orderReasonService = orderReasonService;
    }


    @JsonView(StudentView.Expel.class)
    @PostMapping("/degrees/expels")
    public ResponseEntity expelStudentDegree(@RequestBody StudentExpelDTO studentExpelDTO) {
        List<StudentExpelDTO> studentExpelDTOList;
        try {
            List<StudentExpel> studentExpelList = studentDegreeService.expelStudents(createStudentExpels(studentExpelDTO));
            studentExpelDTOList = new ModelMapper()
                    .map(studentExpelList, new TypeToken<List<StudentExpelDTO>>() {}.getType());
        } catch (Exception exception) {
            return handleException(exception);
        }

        Object[] ids = studentExpelDTOList.stream().map(StudentExpelDTO::getId).toArray();
        URI location = getNewResourceLocation(ids);
        return ResponseEntity.created(location).body(studentExpelDTOList);
    }

    private List<StudentExpel> createStudentExpels(StudentExpelDTO studentExpelDTO) {
        List<Integer> studentDegreeIds = asList(studentExpelDTO.getStudentDegreeIds());
        OrderReason orderReason = orderReasonService.getById(studentExpelDTO.getReasonId());
        return studentDegreeIds.stream()
                .map(id -> createStudentExpel(studentExpelDTO, orderReason, id))
                .collect(Collectors.toList());
    }

    private StudentExpel createStudentExpel(StudentExpelDTO studentExpelDTO, OrderReason orderReason, int studentDegreeId) {
        StudentExpel studentExpel = parseToStudentExpel(studentExpelDTO);
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        studentExpel.setStudentDegree(studentDegree);
        studentExpel.setReason(orderReason);
        return studentExpel;
    }

    private StudentExpel parseToStudentExpel(StudentExpelDTO studentExpelDTO) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(studentExpelDTO, StudentExpel.class);
    }
}

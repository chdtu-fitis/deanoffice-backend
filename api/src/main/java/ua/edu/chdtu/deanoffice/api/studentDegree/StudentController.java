package ua.edu.chdtu.deanoffice.api.studentDegree;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.studentDegree.dto.StudentDegreeDTO;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired StudentDegreeRepository studentDegreeRepository;

    private List<StudentDegreeDTO> getStudentDegreeDTOList(boolean active) {
        List<StudentDegree> studentDegreeList = studentDegreeRepository.findAllByFaculty(active, 1);
        Type type = new TypeToken<List<StudentDegreeDTO>>() {}.getType();
        return new  ModelMapper().map(studentDegreeList, type);
    }

    @GetMapping("/active")
    public List<StudentDegreeDTO> getActiveStudentsDegree( ) {
        return getStudentDegreeDTOList(true);
    }

    @GetMapping("/inactive")
    public List<StudentDegreeDTO> getInactiveStudentsDegree( ) {
        return getStudentDegreeDTOList(false);
    }
}

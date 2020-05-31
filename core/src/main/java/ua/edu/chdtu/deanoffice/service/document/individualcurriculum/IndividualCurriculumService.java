package ua.edu.chdtu.deanoffice.service.document.individualcurriculum;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class IndividualCurriculumService {
    private final StudentDegreeService studentDegreeService;

    public IndividualCurriculumService(StudentDegreeService studentDegreeService) {
        this.studentDegreeService = studentDegreeService;
    }

    public File createIndividualCurriculumDocx(Integer groupId, List<Integer> studentIds) {
        if (Objects.nonNull(groupId)) {

        }
        return null;
    }

    private List<StudentDegree> getStudentDegreesByGroup(Integer groupId) {
        List<StudentDegree> degrees = new ArrayList<>();

        if (Objects.nonNull(groupId) && groupId > 0) {
            degrees.addAll(studentDegreeService.getAllByGroupId(groupId));
        }

        return degrees;
    }

    private List<StudentDegree> getStudentDegreesByStudentIds(List<Integer> studentIds) {
        List<StudentDegree> degrees = new ArrayList<>();

        if (Objects.nonNull(studentIds) && studentIds.size() > 0) {
            degrees.addAll(studentDegreeService.getByStudentIds(studentIds));
        }

        return degrees;
    }
}

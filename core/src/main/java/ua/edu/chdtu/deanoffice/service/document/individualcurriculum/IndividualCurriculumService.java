package ua.edu.chdtu.deanoffice.service.document.individualcurriculum;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Service
public class IndividualCurriculumService {
    private static final String FILE_NAME = "Individual curriculum of the higher educators";

    private final StudentDegreeService studentDegreeService;

    public IndividualCurriculumService(StudentDegreeService studentDegreeService) {
        this.studentDegreeService = studentDegreeService;
    }

    public File createIndividualCurriculumDocx(Integer groupId, List<Integer> studentIds) {
        Set<StudentDegree> studentDegrees = getStudentDegreesByGroup(groupId);
        studentDegrees.addAll(getStudentDegreesByStudentIds(studentIds));


        return null;
    }

    private Set<StudentDegree> getStudentDegreesByGroup(Integer groupId) {
        Set<StudentDegree> degrees = new HashSet<>();

        if (Objects.nonNull(groupId) && groupId > 0) {
            degrees.addAll(studentDegreeService.getAllByGroupId(groupId));
        }

        return degrees;
    }

    private Set<StudentDegree> getStudentDegreesByStudentIds(List<Integer> studentIds) {
        Set<StudentDegree> degrees = new HashSet<>();

        if (Objects.nonNull(studentIds) && studentIds.size() > 0) {
            degrees.addAll(studentDegreeService.getByStudentIds(studentIds));
        }

        return degrees;
    }
}

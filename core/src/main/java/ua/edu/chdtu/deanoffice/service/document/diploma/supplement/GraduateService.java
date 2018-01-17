package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.GroupRepository;

import java.util.List;

@Service
public class GraduateService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CurrentYearRepository currentYearRepository;

    public List<StudentGroup> getGraduateGroups(Integer degreeId) {
        Integer currYear = currentYearRepository.findOne(1).getCurrYear();
        return groupRepository.findGraduateByDegree(degreeId, currYear);
    }
}

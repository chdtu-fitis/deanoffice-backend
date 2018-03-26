package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.CurrentYearRepository;
import ua.edu.chdtu.deanoffice.repository.GroupRepository;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

@Service
public class GroupService {
    private StudentGroupRepository studentGroupRepository;
    private GroupRepository groupRepository;
    private CurrentYearRepository currentYearRepository;

    @Autowired
    public GroupService(
            StudentGroupRepository studentGroupRepository,
            GroupRepository groupRepository,
            CurrentYearRepository currentYearRepository
    ) {
        this.studentGroupRepository = studentGroupRepository;
        this.groupRepository = groupRepository;
        this.currentYearRepository = currentYearRepository;
    }

    public List<StudentGroup> getGroups() {
        return studentGroupRepository.findAllByFaculty(Constants.FACULTY_ID);
    }

    public List<StudentGroup> getGroupsByDegreeAndYear(int degreeId, int year) {
        Integer currYear = currentYearRepository.findOne(1).getCurrYear();
        return groupRepository.findGroupsByDegreeAndYear(degreeId, year, currYear);
    }
}

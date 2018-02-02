package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

import java.util.List;

@Service
public class GroupService {
    private final StudentGroupRepository studentGroupRepository;

    @Autowired
    public GroupService(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<StudentGroup> getGroups() {
        List<StudentGroup> studentGroups = studentGroupRepository.findAllByFaculty(1);
        return studentGroups;
    }
    public List<StudentGroup> getGroupsByCourse(int courseId){
        List<StudentGroup> studentGroups = studentGroupRepository.findAllByCourse(courseId);
        return studentGroups;
    }
}

package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;

@Service
public class StudentGroupService {

    private StudentGroupRepository studentGroupRepository;

    public StudentGroupService(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

    public StudentGroup getById(Integer studentGroupId) {
        return this.studentGroupRepository.findOne(studentGroupId);
    }
}

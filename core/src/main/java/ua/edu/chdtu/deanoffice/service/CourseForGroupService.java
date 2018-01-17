package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;

@Service
public class CourseForGroupService {
    @Autowired
    public CourseForGroupService(TeacherRepository teacherRepository){
        this.teacherRepository = teacherRepository;
    }

    private final TeacherRepository teacherRepository;
}

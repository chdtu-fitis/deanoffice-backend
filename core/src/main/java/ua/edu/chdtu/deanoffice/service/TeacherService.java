package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;

import java.util.List;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public Teacher getTeacher(int teacherId) {
        return teacherRepository.findOne(teacherId);
    }
    public List<Teacher> getTeachers(){
        return teacherRepository.findAllByOrderBySurname();
    }

//    public List<Teacher> getTeachersByIds(List<Integer> ids) {
//        return teacherRepository.findAllByIds(ids);
//    }

    public List<Teacher> getTeachersByActive(boolean active) {
        return teacherRepository.findAllByActive(active);
    }

    public void save(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    public void deleteTeachers(List<Integer> ids) {
        teacherRepository.deleteByIdIn(ids);
    }
}

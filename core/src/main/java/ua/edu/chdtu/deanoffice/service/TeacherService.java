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



//    public List<Teacher> getTeachersByIds(List<Integer> ids) {
//        return teacherRepository.findAllByIds(ids);
//    }

    public List<Teacher> getTeachersByActive(boolean active) {
        return teacherRepository.findAllByActive(active);
    }

    public List<Teacher> getTeachersByActiveAndFacultyId(boolean active, int facultyId) {
        return teacherRepository.findAllByActiveAndFacultyId(active, facultyId);
    }

    public List<Teacher> getTeachersByActiveAndDepartmentId(boolean active, int departmentId) {
        return teacherRepository.findAllByActiveAndDepartmentId(active, departmentId);
    }

    public List<Teacher> getTeachersByActiveAndSurname(boolean active, String surname) {
        return teacherRepository.findAllByActiveAndSurname(active, surname);
    }

    public List<Teacher> getTeachersByActiveAndDepartmentIdAndSurname(boolean active, int departmentId, String surname) {
        return teacherRepository.findAllByActiveAndDepartmentIdAndSurname(active, departmentId, surname);
    }

    public void save(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    public void deleteByIds(List<Integer> ids) {
        teacherRepository.setTeachersInactiveByIds(ids);
    }
}

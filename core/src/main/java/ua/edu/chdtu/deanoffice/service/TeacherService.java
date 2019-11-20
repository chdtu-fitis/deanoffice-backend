package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;
import ua.edu.chdtu.deanoffice.security.FacultyAuthorized;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.util.List;

@Service
public class TeacherService {
    private TeacherRepository teacherRepository;
    private DataVerificationService dataVerificationService;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, DataVerificationService dataVerificationService) {
        this.teacherRepository = teacherRepository;
        this.dataVerificationService = dataVerificationService;
    }

    public Teacher getTeacher(int teacherId) {
        return teacherRepository.findOne(teacherId);
    }

    public List<Teacher> getTeachers(List<Integer> ids) {
        return teacherRepository.findAll(ids);
    }

    public List<Teacher> getActiveFacultyTeachers(boolean active) {
        int facultyId = FacultyUtil.getUserFacultyIdInt();
        return teacherRepository.findAllByActiveAndFacultyId(active, facultyId);
    }

    public List<Teacher> getTeachersByActive(boolean active) {
        return teacherRepository.findAllByActive(active);
    }

    @FacultyAuthorized
    public void deleteByIds(List<Integer> ids) throws OperationCannotBePerformedException {
        if (ids.size() == 0)
            throw new OperationCannotBePerformedException("Не вказані ідентифікатори викладачів!");
        List<Teacher> teachers = getTeachers(ids);
        if (teachers.size() != ids.size())
            throw new OperationCannotBePerformedException("Серед даних ідентифікаторів викладачів є неіснуючі!");
        dataVerificationService.areTeachersActive(teachers);
        teacherRepository.setTeachersInactiveByIds(ids);
    }

    @FacultyAuthorized
    public void restoreByIds(List<Integer> ids) throws OperationCannotBePerformedException {
        if (ids == null || ids.size() == 0)
            throw new OperationCannotBePerformedException("Не вказані ідентифікатори викладачів!");
        List<Teacher> teachers = getTeachers(ids);
        if (teachers.size() != ids.size())
            throw new OperationCannotBePerformedException("Серед даних ідентифікаторів викладачів є існуючі!");
        dataVerificationService.isTeachersNotActive(teachers);
        teachers.forEach(teacher -> teacher.setActive(true));
        teacherRepository.save(teachers);
    }

    @FacultyAuthorized
    public Teacher saveTeacher(Teacher teacher) throws OperationCannotBePerformedException {
        dataVerificationService.isCorrectTeacher(teacher);
        return teacherRepository.save(teacher);
    }

    @FacultyAuthorized
    public Teacher updateTeacher(Teacher teacher) throws OperationCannotBePerformedException {
        if (teacher.getId() == 0)
            throw new OperationCannotBePerformedException("Не можна редагувати дані викладача, що не існує в базі");
        dataVerificationService.isCorrectTeacher(teacher);
        Teacher teacherFromDB = teacherRepository.findOne(teacher.getId());
        if (teacherFromDB == null)
            throw new OperationCannotBePerformedException("Викладача з вказаним ідентифікатором не існує!");
        return teacherRepository.save(teacher);
    }
}


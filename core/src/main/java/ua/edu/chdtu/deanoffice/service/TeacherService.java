package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.DepartmentRepository;
import ua.edu.chdtu.deanoffice.repository.PositionRepository;
import ua.edu.chdtu.deanoffice.repository.ScientificDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;
import ua.edu.chdtu.deanoffice.security.FacultyAuthorized;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.util.List;

@Service
public class TeacherService {
    private TeacherRepository teacherRepository;
    private DataVerificationService dataVerificationService;
    private DepartmentRepository departmentRepository;
    private PositionRepository positionRepository;
    private ScientificDegreeRepository scientificDegreeRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, DataVerificationService dataVerificationService,
                          DepartmentRepository departmentRepository, PositionRepository positionRepository,
                          ScientificDegreeRepository scientificDegreeRepository) {
        this.teacherRepository = teacherRepository;
        this.dataVerificationService = dataVerificationService;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.scientificDegreeRepository = scientificDegreeRepository;
    }

    public Teacher getTeacher(int teacherId) {
        return teacherRepository.findOne(teacherId);
    }

    public List<Teacher> getTeachers(List<Integer> ids) {
        return teacherRepository.findAll(ids);
    }

    public List<Teacher> getFacultyTeachers(boolean active) {
        int facultyId = FacultyUtil.getUserFacultyIdInt();
        return teacherRepository.findAllByActiveAndFacultyId(active, facultyId);
    }

    public List<Teacher> getActiveFacultyTeachersBySurnamePart(String searchStr) {
        int facultyId = FacultyUtil.getUserFacultyIdInt();
        return teacherRepository.findActiveBySurnamePartAndFacultyId(searchStr.toLowerCase(), facultyId);
    }

    public List<Teacher> getTeachersByActive(boolean active) {
        return teacherRepository.findAllByActive(active);
    }

    @FacultyAuthorized
    public void deleteByIds(List<Integer> ids) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        if (ids.size() == 0)
            throw new OperationCannotBePerformedException("Не вказані ідентифікатори викладачів!");
        List<Teacher> teachers = getTeachers(ids);
        if (teachers.size() != ids.size())
            throw new OperationCannotBePerformedException("Серед даних ідентифікаторів викладачів є неіснуючі!");
        dataVerificationService.areTeachersActive(teachers);
        teacherRepository.setTeachersInactiveByIds(ids);
    }

    @FacultyAuthorized
    public void restoreByIds(List<Integer> ids) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        if (ids == null || ids.size() == 0)
            throw new OperationCannotBePerformedException("Не вказані ідентифікатори викладачів!");
        List<Teacher> teachers = getTeachers(ids);
        if (teachers.size() != ids.size())
            throw new OperationCannotBePerformedException("Серед даних ідентифікаторів викладачів є існуючі!");
        dataVerificationService.isTeachersNotActive(teachers);
        teachers.forEach(teacher -> teacher.setActive(true));
        teacherRepository.save(teachers);
    }

    public Teacher createTeacher(Teacher teacher) {
        setPositionAndDepartmentAndScientificDegreeFromDBForCreate(teacher);
        return teacherRepository.save(teacher);
    }

    //UnauthorizedFacultyDataException потрібен для перевірки права доступу в аспектах
    @FacultyAuthorized
    public Teacher updateTeacher(Teacher teacher) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        Teacher teacherFromDB = teacherRepository.findOne(teacher.getId());
        if (teacherFromDB == null) {
            throw new OperationCannotBePerformedException("Викладача з вказаним ідентифікатором не існує!");
        } else {
            setPositionFromDBForUpdate(teacher, teacherFromDB);
            setDepartmentFromDBForUpdate(teacher, teacherFromDB);
            setScientificDegreeFromDBForUpdate(teacher, teacherFromDB);
        }
        return teacherRepository.save(teacher);
    }

    private void setPositionFromDBForUpdate(Teacher teacher, Teacher teacherFromDB) {
        if (teacher.getPosition().getId() == teacherFromDB.getPosition().getId()) {
            teacher.setPosition(teacherFromDB.getPosition());
        } else {
            teacher.setPosition(positionRepository.findOne(teacher.getPosition().getId()));
        }
    }

    private void setDepartmentFromDBForUpdate(Teacher teacher, Teacher teacherFromDB) {
        if (teacher.getDepartment().getId() == teacherFromDB.getDepartment().getId()) {
            teacher.setDepartment(teacherFromDB.getDepartment());
        } else {
            teacher.setDepartment(departmentRepository.findOne(teacher.getDepartment().getId()));
        }
    }

    private void setScientificDegreeFromDBForUpdate(Teacher teacher, Teacher teacherFromDB) {
        if (teacher.getScientificDegree() != null) {
            if (teacherFromDB.getScientificDegree() != null && teacher.getScientificDegree().getId() == teacherFromDB.getScientificDegree().getId()) {
                teacher.setScientificDegree(teacherFromDB.getScientificDegree());
            } else {
                teacher.setScientificDegree(scientificDegreeRepository.findOne(teacher.getScientificDegree().getId()));
            }
        }
    }

    private void setPositionAndDepartmentAndScientificDegreeFromDBForCreate(Teacher teacher) {
        teacher.setPosition(positionRepository.findOne(teacher.getPosition().getId()));
        teacher.setDepartment(departmentRepository.findOne(teacher.getDepartment().getId()));
        if (teacher.getScientificDegree() != null) {
            teacher.setScientificDegree(scientificDegreeRepository.findOne(teacher.getScientificDegree().getId()));
        }
    }
}


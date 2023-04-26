package ua.edu.chdtu.deanoffice.service;

import org.apache.regexp.RE;
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
        return teacherRepository.findById(teacherId).get();
    }

    public List<Teacher> getTeachers(List<Integer> ids) {
        return teacherRepository.findAllById(ids);
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
        List<Teacher> teachers = getTeachers(ids);
        if (teachers.size() != ids.size())
            throw new OperationCannotBePerformedException("Серед даних ідентифікаторів викладачів є неіснуючі!");
        teacherRepository.setTeachersInactiveByIds(ids);
    }

    @FacultyAuthorized
    public void restoreByIds(List<Integer> ids) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        List<Teacher> teachers = getTeachers(ids);
        if (teachers.size() != ids.size())
            throw new OperationCannotBePerformedException("Серед даних ідентифікаторів викладачів є існуючі!");
        teachers.forEach(teacher -> teacher.setActive(true));
        teacherRepository.saveAll(teachers);
    }

    public Teacher createTeacher(Teacher teacher) {
        setPositionAndDepartmentAndScientificDegreeFromDBForCreate(teacher);
        return teacherRepository.save(teacher);
    }

    //UnauthorizedFacultyDataException потрібен для перевірки права доступу в аспектах
    @FacultyAuthorized
    public Teacher updateTeacher(Teacher teacher) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        Teacher teacherFromDB = teacherRepository.findById(teacher.getId()).get();
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
            teacher.setPosition(positionRepository.findById(teacher.getPosition().getId()).get());
        }
    }

    private void setDepartmentFromDBForUpdate(Teacher teacher, Teacher teacherFromDB) {
        if (teacher.getDepartment().getId() == teacherFromDB.getDepartment().getId()) {
            teacher.setDepartment(teacherFromDB.getDepartment());
        } else {
            teacher.setDepartment(departmentRepository.findById(teacher.getDepartment().getId()).get());
        }
    }

    private void setScientificDegreeFromDBForUpdate(Teacher teacher, Teacher teacherFromDB) {
        if (teacher.getScientificDegree() != null) {
            if (teacherFromDB.getScientificDegree() != null && teacher.getScientificDegree().getId() == teacherFromDB.getScientificDegree().getId()) {
                teacher.setScientificDegree(teacherFromDB.getScientificDegree());
            } else {
                teacher.setScientificDegree(scientificDegreeRepository.findById(teacher.getScientificDegree().getId()).get());
            }
        }
    }

    private void setPositionAndDepartmentAndScientificDegreeFromDBForCreate(Teacher teacher) {
        teacher.setPosition(positionRepository.findById(teacher.getPosition().getId()).get());
        teacher.setDepartment(departmentRepository.findById(teacher.getDepartment().getId()).get());
        if (teacher.getScientificDegree() != null) {
            teacher.setScientificDegree(scientificDegreeRepository.findById(teacher.getScientificDegree().getId()).get());
        }
    }

    public Teacher getTeacherBySurnameAndInitialsAndDepartment(String[] teacherSurnameAndInitials, String departmentName) {
        Teacher teacher;
        List<Teacher> teachersFoundList = teacherRepository.findAllBySurnameAndInitialsAndDepartment(teacherSurnameAndInitials[0], teacherSurnameAndInitials[1], teacherSurnameAndInitials[2], departmentName);
        teacher = getTeacherFromList(teachersFoundList);
        if (teacher == null) {
            List<Teacher> teachersNoDepartmentList = teacherRepository.findAllBySurnameAndInitials(teacherSurnameAndInitials[0], teacherSurnameAndInitials[1], teacherSurnameAndInitials[2]);
            teacher = getTeacherFromList(teachersNoDepartmentList);
        }
        if (teacher == null) {
            return null;
            // TODO: FORM EXCEPTION REPORT
        }
        return teacher;
    }

    private Teacher getTeacherFromList(List<Teacher> teachersFoundList) {
        if (getTeachersNumber(teachersFoundList.size()) == Result.ONE) {
                return teachersFoundList.get(0);
        }
        return null;
    }

    private Result getTeachersNumber(int teacherListSize) {
        if (teacherListSize == 0) {
            return Result.NONE;
        } else if (teacherListSize > 1) {
            return Result.MORE_THAN_ONE;
        } else {
            return Result.ONE;
        }
    }

    private enum Result {
        NONE,
        ONE,
        MORE_THAN_ONE
    }
}


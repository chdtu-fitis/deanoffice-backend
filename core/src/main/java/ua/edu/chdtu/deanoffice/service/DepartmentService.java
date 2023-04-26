package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.DepartmentRepository;
import ua.edu.chdtu.deanoffice.security.FacultyAuthorized;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllUniversityDepartments() {
        return departmentRepository.getAllByActive(true);
    }

    public List<Department> getFacultyDepartmentsByActive(boolean active) {
        int facultyId = FacultyUtil.getUserFacultyIdInt();
        if (facultyId == Constants.FOREIGN_STUDENTS_FACULTY_ID || facultyId == Constants.PHD_FACULTY_ID) {
            return departmentRepository.getAllByActive(active);
        } else {
            return departmentRepository.getAllByActive(active, facultyId);
        }
    }

    public Department getById(Integer departmentId) {
        return this.departmentRepository.findById(departmentId).get();
    }

    public Department getByAbbr(String departmentAbbr) {
        if (this.departmentRepository.getAllByAbbr(true, departmentAbbr).size() == 1) {
            return this.departmentRepository.getAllByAbbr(true, departmentAbbr).get(0);
        }
        return null;
    }

    @FacultyAuthorized
    public Department save(Department department) throws UnauthorizedFacultyDataException {
        return departmentRepository.save(department);
    }

    @FacultyAuthorized
    public void delete(@Validated @NotNull(message="Не можна видалити не існуючу кафедру")
                                   Department department) throws UnauthorizedFacultyDataException {
        department.setActive(false);
        departmentRepository.save(department);
    }

    @FacultyAuthorized
    public void restore(@Validated @NotNull(message="Не можна відновити не існуючу кафедру")
                                    Department department) throws UnauthorizedFacultyDataException {
        department.setActive(true);
        departmentRepository.save(department);
    }
}

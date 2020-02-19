package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.DepartmentRepository;
import ua.edu.chdtu.deanoffice.security.FacultyAuthorized;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getFacultyDepartmentsByActive(boolean active) {
        int facultyId = FacultyUtil.getUserFacultyIdInt();
        if (facultyId == Constants.FOREIGN_STUDENTS_FACULTY_ID) {
            return departmentRepository.getAllByActive(active);
        } else {
            return departmentRepository.getAllByActive(active, facultyId);
        }
    }

    public Department getById(Integer departmentId) {
        return this.departmentRepository.findOne(departmentId);
    }

    @FacultyAuthorized
    public Department save(Department department) throws UnauthorizedFacultyDataException {
        return departmentRepository.save(department);
    }

    @FacultyAuthorized
    public void delete(Department department) throws UnauthorizedFacultyDataException {
        department.setActive(false);
        departmentRepository.save(department);
    }

    @FacultyAuthorized
    public void restore(Department department) throws UnauthorizedFacultyDataException {
        department.setActive(true);
        departmentRepository.save(department);
    }
}
